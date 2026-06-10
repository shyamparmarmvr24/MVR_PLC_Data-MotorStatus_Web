package com.mvrtechnology.plcdata.scheduler;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.LiveSolarDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.service.LiveSolarService;
import com.mvrtechnology.plcdata.service.PlcConnectionPool;
import com.mvrtechnology.plcdata.service.PlcConnectionService;
import com.mvrtechnology.plcdata.sse.LiveSolarSseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private ExecutorService motorExecutor;

    @Scheduled(fixedRate = 1000)
    public void pushLiveData()
    {
        if(sseService.getSubscribedPlants().isEmpty())
        {
            return;
        }

        for(Integer plantId : sseService.getSubscribedPlants())
        {
            motorExecutor.submit(() ->
                    processPlant(plantId));
        }
    }

    private void processPlant(Integer plantId)
    {
        PlantDetails plant =
                plantCache.get(plantId);

        if(plant == null)
        {
            return;
        }

        try
        {
            TCPMasterConnection connection =
                    plcConnectionPool.getConnection(
                            plantId,
                            plant.getPlcIp(),
                            plant.getPlcPort());

            synchronized (
                    plcConnectionPool.getLock(
                            plantId))
            {
                LiveSolarDTO dto =
                        liveSolarService.readLiveData(
                                plant,
                                connection);

                sseService.send(
                        plantId,
                        dto);
            }
        }
        catch(Exception ex)
        {
            plcConnectionPool.close(plantId);
        }
    }
}