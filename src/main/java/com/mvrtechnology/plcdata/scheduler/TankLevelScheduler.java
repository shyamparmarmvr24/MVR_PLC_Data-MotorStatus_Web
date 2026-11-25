package com.mvrtechnology.plcdata.scheduler;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.service.ITankLevelDataService;
import com.mvrtechnology.plcdata.service.PlcConnectionPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TankLevelScheduler {

    private static final List<Integer> TARGET_PLANT_IDS =
            List.of(
                    12,
                    71,
                    86,
                    20
            );


    @Autowired
    private PlantCache plantCache;


    @Autowired
    private PlcConnectionPool plcConnectionPool;


    @Autowired
    private ITankLevelDataService tankLevelDataService;


    @Scheduled(fixedDelay = 60000)
    public void fetchAndStoreTankLevel() {
        for (Integer plantId : TARGET_PLANT_IDS) {
            try {
                PlantDetails plant =
                        plantCache.get(plantId);


                if (plant == null) {
                    log.warn(
                            "Plant ID {} not found in PlantCache",
                            plantId
                    );

                    continue;
                }

                synchronized (plcConnectionPool.getLock(plantId)) {
                    TCPMasterConnection connection =
                            plcConnectionPool.getConnection(
                                    plant.getPlantId(),
                                    plant.getPlcIp(),
                                    plant.getPlcPort()
                            );


                    tankLevelDataService.fetchAndSaveTankLevel(
                            plant,
                            connection
                    );
                }
            } catch (Exception e) {

                plcConnectionPool.close(plantId);

                log.error(
                        "Unexpected tank level fetch error. plantId={} reason={}",
                        plantId,
                        e.getMessage()
                );
            }
        }
    }
}