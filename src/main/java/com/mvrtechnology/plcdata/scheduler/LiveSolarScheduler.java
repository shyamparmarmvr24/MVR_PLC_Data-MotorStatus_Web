package com.mvrtechnology.plcdata.scheduler;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.LiveSolarDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.service.LiveSolarService;
import com.mvrtechnology.plcdata.service.PlcConnectionPool;
import com.mvrtechnology.plcdata.sse.LiveSolarSseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Component
public class LiveSolarScheduler
{
    @Autowired
    private PlantCache plantCache;

    @Autowired
    private LiveSolarService liveSolarService;

    @Autowired
    private LiveSolarSseService sseService;

    @Autowired
    private PlcConnectionPool plcConnectionPool;

    @Autowired
    @Qualifier("liveSolarExecutor")
    private ExecutorService motorExecutor;

    private final ConcurrentHashMap<Integer,Boolean> plantRunning = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 1000)
    public void pushLiveData()
    {
        if(sseService.getSubscribedPlants().isEmpty())
        {
            return;
        }

        for(Integer plantId : sseService.getSubscribedPlants())
        {
            if(plantRunning.putIfAbsent(plantId, true) == null)
            {
                motorExecutor.submit(() ->
                {
                    try
                    {
                        processPlant(plantId);
                    }
                    finally
                    {
                        plantRunning.remove(plantId);
                    }
                });
            }
        }
    }

    private void processPlant(Integer plantId)
    {
        PlantDetails plant = plantCache.get(plantId);

        if(plant == null)
        {
            return;
        }

        try
        {
            synchronized (plcConnectionPool.getLock(plantId))
            {
                TCPMasterConnection connection = plcConnectionPool.getConnection(plantId, plant.getPlcIp(), plant.getPlcPort());
                LiveSolarDTO dto = liveSolarService.readLiveData(plant, connection);

                sseService.send(plantId, dto);
            }
        }
        catch(Exception ex)
        {
            plcConnectionPool.close(plantId);
        }
    }
}