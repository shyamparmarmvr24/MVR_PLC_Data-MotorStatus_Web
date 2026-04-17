package com.mvrtechnology.plcdata.scheduler;
import com.mvrtechnology.plcdata.cache.MotorStatusCache;
import com.mvrtechnology.plcdata.dtos.MotorStatusDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.repository.IPlantDetailsRepo;
import com.mvrtechnology.plcdata.service.MotorStatusService;
import com.mvrtechnology.plcdata.service.PlcConnectionService;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MotorScheduler {

    private final IPlantDetailsRepo plantRepo;
    private final PlcConnectionService connectionService;
    private final MotorStatusService motorService;
    private final MotorStatusCache cache;

    public MotorScheduler(IPlantDetailsRepo plantRepo,
                          PlcConnectionService connectionService,
                          MotorStatusService motorService,
                          MotorStatusCache cache) {
        this.plantRepo = plantRepo;
        this.connectionService = connectionService;
        this.motorService = motorService;
        this.cache = cache;
    }

    @Scheduled(fixedRate = 1000)
    public void fetchMotorData() {

        List<PlantDetails> plants = plantRepo.findAll();

        for (PlantDetails plant : plants) {

            TCPMasterConnection connection = null;

            try {
                connection = connectionService.getConnection(
                        plant.getPlcIp(),
                        plant.getPlcPort()
                );

                MotorStatusDTO data = motorService.fetchMotorStatus(connection);

                cache.update(plant.getPlantId(), data);

            } catch (Exception e) {
                System.out.println("Motor Fetch Failed: " + plant.getPlantName()+" "+e.getMessage());
            } finally {
                try {
                    if (connection != null && connection.isConnected())
                        connection.close();
                } catch (Exception ignored) {}
            }
        }
    }
}