package com.mvrtechnology.plcdata.scheduler;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.LiveEffluentDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.service.LiveEffluentService;
import com.mvrtechnology.plcdata.service.PlcConnectionPool;
import com.mvrtechnology.plcdata.sse.LiveEffluentSseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Component
public class LiveEffluentScheduler
{
    @Autowired
    private PlantCache plantCache;

    @Autowired
    private PlcConnectionPool plcConnectionPool;

    @Autowired
    private LiveEffluentService liveService;

    @Autowired
    private LiveEffluentSseService sseService;

    @Autowired
    @Qualifier("liveEffluentExecutor")
    private ExecutorService motorExecutor;

    private final ConcurrentHashMap<Integer,Boolean> plantRunning = new ConcurrentHashMap<>();

    @Autowired
    private com.mvrtechnology.plcdata.service.PlcStatusService plcStatusService;

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
                try
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
                catch (java.util.concurrent.RejectedExecutionException rex)
                {
                    plantRunning.remove(plantId);
                }
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
//            TCPMasterConnection connection =
//                    plcConnectionPool.getConnection(plantId, plant.getPlcIp(), plant.getPlcPort());

            synchronized (plcConnectionPool.getLock(plantId))
            {
                TCPMasterConnection connection =
                        plcConnectionPool.getConnection(plantId, plant.getPlcIp(), plant.getPlcPort());
                LiveEffluentDTO dto = liveService.readLiveData(plant, connection);
                sseService.send(plantId, dto);
            }
        }
        catch (Exception ex)
        {
            plcConnectionPool.close(plantId);
        }
    }
}