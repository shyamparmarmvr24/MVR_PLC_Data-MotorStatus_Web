package com.mvrtechnology.plcdata.scheduler;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.repository.*;
import com.mvrtechnology.plcdata.service.*;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class PlcScheduler
{
    @Autowired
    private IPlantDetailsRepo plantRepo;

    @Autowired
    private PlcConnectionService connectionService;

    @Autowired
    private ISolarAndEnergyDataService solarAndEnergyRepo;

    @Autowired
    private IEffluentDataService effluentRepo;

    @Autowired
    private ExecutorService executor;

    private final ConcurrentHashMap<Integer, Boolean> solarOfflinePlants = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 300000)
    public void fetchAllPlantsData()
    {
        List<PlantDetails> plants = plantRepo.findAll();

        for (PlantDetails plant : plants)
        {
            if (!executor.isShutdown())
            {
                executor.submit(() -> processPlantWithRetry(plant));
            }
        }
    }

    private void processPlant(PlantDetails plant)
    {
        TCPMasterConnection connection = null;

        try
        {
            connection = connectionService.getConnection(plant.getPlcIp(), plant.getPlcPort());
            solarAndEnergyRepo.fetchAndSaveSolarAndEnergy(plant,connection);
            if(solarOfflinePlants.remove(plant.getPlantId()) != null)
            {
                log.info(
                        "SOLAR PLC ONLINE : {} - {}",
                        plant.getPlantId(),
                        plant.getPlantName());
            }
        }
        catch (Exception e)
        {
            if(solarOfflinePlants.putIfAbsent(plant.getPlantId(), true) == null)
            {
                log.warn(
                        "SOLAR PLC OFFLINE : {} - {}",
                        plant.getPlantId(),
                        plant.getPlantName());
            }
        }
        finally
        {
            try
            {
                if (connection != null && connection.isConnected())
                    connection.close();
            }
            catch (Exception ignored) {}
        }
    }

    private void processPlantWithRetry(PlantDetails plant)
    {
        int attempts = 0;

        while (attempts < 2)
        {
            try
            {
                processPlant(plant);
                return;
            }
            catch (Exception e)
            {
                attempts++;
            }
        }
    }
}