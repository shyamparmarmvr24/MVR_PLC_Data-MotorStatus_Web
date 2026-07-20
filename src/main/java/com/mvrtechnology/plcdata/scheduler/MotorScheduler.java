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
import com.mvrtechnology.plcdata.service.PlcStatusService;
import com.mvrtechnology.plcdata.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

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
    @Qualifier("motorExecutor")
    private ExecutorService executor;
    @Autowired
    private SseService sseService;
    @Autowired
    private PlcStatusService plcStatusService;

    private final ConcurrentHashMap<Integer, Boolean> plantRunning = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, PlantInfoDto> plantInfoCache = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 1000)
    public void run()
    {
        for (PlantDetails plant : plantCache.getAll())
        {
            if (plantRunning.putIfAbsent(plant.getPlantId(), true) == null)
            {
                try
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
                catch (RejectedExecutionException rex)
                {
                    plantRunning.remove(plant.getPlantId());
                }
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void publishAllPlantStatus()
    {
        if(plantInfoCache.isEmpty())
        {
            return;
        }

        AllPlantStatusDTO dto =
                new AllPlantStatusDTO();

        dto.setPlants(
                new ArrayList<>(plantInfoCache.values()));

        sseService.sendPlantInfoUpdate(dto);

        plantInfoCache.clear();
    }

    private void processPlant(PlantDetails plant)
    {
        try
        {
            synchronized (pool.getLock(plant.getPlantId()))
            {
                TCPMasterConnection conn = pool.getConnection(plant.getPlantId(), plant.getPlcIp(), plant.getPlcPort());
                boolean alive =
                        service.heartbeat(conn);

                if(!alive)
                {
                    pool.close(
                            plant.getPlantId());

                    plcStatusService.reportFailure(
                            plant.getPlantId());

                    return;
                }

                MotorStatusDTO data = service.fetchMotorStatus(conn);

                boolean changed = plcStatusService.reportSuccess(plant.getPlantId());
                cache.update(plant.getPlantId(), data);

                PlantMotorResponseDTO response = new PlantMotorResponseDTO();
                response.setPlantId(plant.getPlantId());
                response.setPlantName(plant.getPlantName());
                response.setZone(plant.getZone());
                response.setPlcOnline(plcStatusService.isOnline(plant.getPlantId()));
                response.setMotorStatus(data);

                sseService.send(plant.getPlantId(), response);

                if (changed)
                {
                    PlantInfoDto dto = new PlantInfoDto();
                    dto.setPlantId(plant.getPlantId());
                    dto.setPlantName(plant.getPlantName());
                    dto.setZone(plant.getZone());
                    dto.setPlcStatusSM400(true);

                    plantInfoCache.put(plant.getPlantId(), dto);
                }
            }
        }
        catch(Exception ex)
        {
            pool.close(
                    plant.getPlantId());

            boolean changed =
                    plcStatusService.reportFailure(
                            plant.getPlantId());

            cache.remove(
                    plant.getPlantId());

            if(changed)
            {
                PlantInfoDto dto =
                        new PlantInfoDto();

                dto.setPlantId(
                        plant.getPlantId());

                dto.setPlantName(
                        plant.getPlantName());

                dto.setZone(
                        plant.getZone());

                dto.setPlcStatusSM400(
                        false);

                plantInfoCache.put(
                        plant.getPlantId(),
                        dto);

            }
        }
    }
}