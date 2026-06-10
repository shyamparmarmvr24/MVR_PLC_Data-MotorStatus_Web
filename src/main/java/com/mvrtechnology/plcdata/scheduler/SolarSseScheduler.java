package com.mvrtechnology.plcdata.scheduler;
import com.mvrtechnology.plcdata.dtos.PlantSolarResponseDTO;
import com.mvrtechnology.plcdata.repository.IPlantDetailsRepo;
import com.mvrtechnology.plcdata.service.ISolarAndEnergyDataService;
import com.mvrtechnology.plcdata.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SolarSseScheduler
{
    @Autowired
    private ISolarAndEnergyDataService solarService;

    @Autowired
    private IPlantDetailsRepo plantRepo;

    @Autowired
    private SseService sseService;

    @Scheduled(cron = "30 */5 * * * *")
    public void pushSolarData()
    {
        plantRepo.findAll().forEach(plant ->
        {
            try
            {
                PlantSolarResponseDTO dto = solarService.getLatestByPlant(plant.getPlantId());

                if(dto != null)
                {
                    sseService.sendSolarUpdate(plant.getPlantId(), dto);
                }
            }
            catch (Exception ignored)
            {
            }
        });
    }
}