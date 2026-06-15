package com.mvrtechnology.plcdata.scheduler;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.service.IEffluentDataService;
import com.mvrtechnology.plcdata.service.PlcConnectionService;
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
    private PlcConnectionService connectionService;

    @Autowired
    private IEffluentDataService effluentService;

    private final ExecutorService executor = Executors.newFixedThreadPool(50);

    private volatile boolean running = false;

    @Scheduled(fixedDelay = 30000)
    public void fetchEffluentData()
    {
        if(running)
        {
            return;
        }

        running = true;

        try
        {
            for(PlantDetails plant : plantCache.getAll())
            {
                executor.submit(
                        () -> processPlant(plant));
            }
        }
        finally
        {
            running = false;
        }
    }

    private void processPlant(PlantDetails plant)
    {
        TCPMasterConnection connection = null;

        try
        {
            connection = connectionService.getConnection(plant.getPlcIp(), plant.getPlcPort());

            effluentService.fetchAndSaveEffluentData(plant, connection);
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                if(connection != null && connection.isConnected())
                {
                    connection.close();
                }
            }
            catch (Exception ignored)
            {
            }
        }
    }
}