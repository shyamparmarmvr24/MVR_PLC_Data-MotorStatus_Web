package com.mvrtechnology.plcdata.scheduler;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.service.IEffluentDataService;
import com.mvrtechnology.plcdata.service.PlcConnectionPool;
import com.mvrtechnology.plcdata.service.PlcStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.*;

@Component
public class EffluentFetchScheduler
{
    @Autowired
    private PlantCache plantCache;

    @Autowired
    private PlcConnectionPool connectionPool;

    @Autowired
    private IEffluentDataService effluentService;

    @Autowired
    private PlcStatusService plcStatusService;

    private final ExecutorService executor = Executors.newFixedThreadPool(50);
    private volatile boolean running = false;

    @Scheduled(fixedDelay = 30000)
    public void fetchEffluentData()
    {
        if (running) return;
        running = true;
        try
        {
            for (PlantDetails plant : plantCache.getAll())
            {
                executor.submit(() -> processPlant(plant));
            }
        }
        finally
        {
            running = false;
        }
    }

    private void processPlant(PlantDetails plant)
    {
        try
        {
            synchronized (connectionPool.getLock(plant.getPlantId()))
            {
                TCPMasterConnection connection =
                        connectionPool.getConnection(plant.getPlantId(), plant.getPlcIp(), plant.getPlcPort());
                effluentService.fetchAndSaveEffluentData(plant, connection);
            }
        }
        catch (Exception e)
        {
            connectionPool.close(plant.getPlantId());
        }
    }
}