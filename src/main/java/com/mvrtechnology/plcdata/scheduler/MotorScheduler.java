package com.mvrtechnology.plcdata.scheduler;
import com.mvrtechnology.plcdata.cache.MotorStatusCache;
import com.mvrtechnology.plcdata.cache.PlantCache;
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
import java.util.concurrent.ExecutorService;

@Component
public class MotorScheduler
{

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


    @Scheduled(fixedDelay = 1500)
    public void run()
    {

        for (PlantDetails plant : plantCache.getAll()) {

            executor.submit(() -> {

                try {
                    TCPMasterConnection conn = pool.getConnection(plant.getPlcIp(), plant.getPlcPort());

                    MotorStatusDTO data = service.fetchMotorStatus(conn);

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
                    dto.setPlcStatusSM400(data.getSm400());

                    sseService.sendPlantInfoUpdate(dto);

                }
                catch (Exception e)
                {
                    System.out.println("Motor FAIL: " + plant.getPlantName()+" "+e.getMessage());

                    // GET LAST KNOWN VALUE
                    MotorStatusDTO lastData = cache.get(plant.getPlantId());

                    PlantInfoDto dto = new PlantInfoDto();
                    dto.setPlantId(plant.getPlantId());
                    dto.setPlantName(plant.getPlantName());
                    dto.setZone(plant.getZone());

                    if (lastData != null)
                    {
                        dto.setPlcStatusSM400(lastData.getSm400());
                    }
                    else
                    {
                        dto.setPlcStatusSM400(null);
                    }

                    sseService.sendPlantInfoUpdate(dto);
                }
                finally
                {
                    pool.close();
                }
            });
        }
    }
}