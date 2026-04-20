package com.mvrtechnology.plcdata.scheduler;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.PlantEffluentResponseDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.service.IEffluentDataService;
import com.mvrtechnology.plcdata.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EffluentScheduler
{
    @Autowired
    private PlantCache plantCache;
    @Autowired
    private IEffluentDataService service;
    @Autowired
    private SseService sseService;

    @Scheduled(fixedRate = 360000)
    public void push()
    {

        for (PlantDetails plant : plantCache.getAll())
        {
            try
            {
                PlantEffluentResponseDTO latest = service.getLatestByPlant(plant.getPlantId());
                sseService.sendEffluent(plant.getPlantId(), latest);
            }
            catch (Exception e)
            {
                System.out.println("EFFLUENT FAIL: " + plant.getPlantName());
            }
        }
    }
}
