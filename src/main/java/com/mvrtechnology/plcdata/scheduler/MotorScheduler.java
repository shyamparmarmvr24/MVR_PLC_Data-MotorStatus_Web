package com.mvrtechnology.plcdata.scheduler;
import com.mvrtechnology.plcdata.cache.MotorStatusCache;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.AllPlantStatusDTO;
import com.mvrtechnology.plcdata.dtos.MotorStatusDTO;
import com.mvrtechnology.plcdata.dtos.PlantInfoDto;
import com.mvrtechnology.plcdata.dtos.PlantMotorResponseDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.service.MotorStatusService;
import com.mvrtechnology.plcdata.service.PlcConnectionPool;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Component
public class MotorScheduler {

    @Autowired
    private PlantCache plantCache;
    @Autowired
    private PlcConnectionPool pool;
    @Autowired
    private MotorStatusService service;
    @Autowired
    private MotorStatusCache cache;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private SseService sseService;

    private final ConcurrentHashMap<Integer, Boolean> plantRunning = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, PlantInfoDto> plantInfoCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, Integer> failureCounts = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, Boolean> plcOnlineStatus = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 1000)
    public void run()
    {
        for (PlantDetails plant : plantCache.getAll()) {
            if (plantRunning.putIfAbsent(plant.getPlantId(), true) == null)
            {
                executor.submit(() ->
                {
                    try
                    {
                        processPlant(plant);
                    }
                    finally
                    {
                        plantRunning.remove(plant.getPlantId());
                    }
                });
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void publishAllPlantStatus()
    {
        if (plantInfoCache.isEmpty())
        {
            return;
        }

        AllPlantStatusDTO response = new AllPlantStatusDTO();

        response.setPlants(new ArrayList<>(plantInfoCache.values()));

        sseService.sendPlantInfoUpdate(response);
    }

    private void processPlant(PlantDetails plant)
    {
        try
        {
            TCPMasterConnection conn = pool.getConnection(plant.getPlantId(), plant.getPlcIp(), plant.getPlcPort());

            synchronized (pool.getLock(plant.getPlantId()))
            {
                MotorStatusDTO data = service.fetchMotorStatus(conn);

                failureCounts.put(plant.getPlantId(), 0);

                plcOnlineStatus.put(plant.getPlantId(), true);

                cache.update(plant.getPlantId(), data);

                PlantMotorResponseDTO response = new PlantMotorResponseDTO();

                response.setPlantId(plant.getPlantId());
                response.setPlantName(plant.getPlantName());
                response.setZone(plant.getZone());
                response.setMotorStatus(data);

                sseService.send(plant.getPlantId(), response);

                PlantInfoDto dto = new PlantInfoDto();

                dto.setPlantId(plant.getPlantId());
                dto.setPlantName(plant.getPlantName());
                dto.setZone(plant.getZone());
//                dto.setPlcStatusSM400(data.getSm400());
                dto.setPlcStatusSM400(plcOnlineStatus.getOrDefault(plant.getPlantId(), false));

                plantInfoCache.put(plant.getPlantId(), dto);
            }
        }
        catch (Exception e)
        {
            pool.close(plant.getPlantId());

            int failures = failureCounts.merge(plant.getPlantId(), 1, Integer::sum);

            if(failures >= 3)
            {
                plcOnlineStatus.put(plant.getPlantId(), false);
            }

            PlantInfoDto dto = new PlantInfoDto();

            dto.setPlantId(plant.getPlantId());

            dto.setPlantName(plant.getPlantName());

            dto.setZone(plant.getZone());

            dto.setPlcStatusSM400(plcOnlineStatus.getOrDefault(plant.getPlantId(), false));

            plantInfoCache.put(plant.getPlantId(), dto);
        }
    }
}