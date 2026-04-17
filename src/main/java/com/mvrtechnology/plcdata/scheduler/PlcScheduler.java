package com.mvrtechnology.plcdata.scheduler;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.repository.*;
import com.mvrtechnology.plcdata.service.*;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
public class PlcScheduler
{
    @Autowired
    private IPlantDetailsRepo plantRepo;

    @Autowired
    private PlcConnectionService connectionService;

    @Autowired
    private ISolarAndEnergyDataRepoImpl solarAndEnergyRepo;

    @Autowired
    private IEffluentDataRepoImpl effluentRepo;

    private final ExecutorService executor =
            new java.util.concurrent.ThreadPoolExecutor(
                    20, 20,
                    0L, java.util.concurrent.TimeUnit.MILLISECONDS,
                    new java.util.concurrent.ArrayBlockingQueue<>(200),
                    new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()
            );

    @Scheduled(fixedRate = 300000) // 5 minutes
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
            effluentRepo.fetchAndSaveEffluentData(plant,connection);
        }
        catch (Exception e)
        {
            System.out.println("PLC FAILED : " + plant.getPlantName() +" "+e.getMessage());
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
                System.out.println("Retry " + attempts + " for " + plant.getPlantName());
            }
        }

        System.out.println("Fail To Fetch Data : " + plant.getPlantName());
    }
}