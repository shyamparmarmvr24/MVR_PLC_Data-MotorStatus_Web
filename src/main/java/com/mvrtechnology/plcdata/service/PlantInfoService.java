package com.mvrtechnology.plcdata.service;
import com.mvrtechnology.plcdata.cache.MotorStatusCache;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.PlantInfoDto;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlantInfoService {

    @Autowired
    private PlantCache plantCache;
    @Autowired
    private MotorStatusCache motorCache;

    public List<PlantInfoDto> getAllPlants() {

        List<PlantInfoDto> response = new ArrayList<>();

        for (PlantDetails plant : plantCache.getAll()) {

            PlantInfoDto dto = new PlantInfoDto();

            dto.setPlantId(plant.getPlantId());
            dto.setPlantName(plant.getPlantName());
            dto.setZone(plant.getZone());

            Boolean sm400 = null;

            var motor = motorCache.get(plant.getPlantId());

            if (motor != null) {
                sm400 = motor.getSm400();
            }

            dto.setPlcStatusSM400(sm400);

            response.add(dto);
        }

        return response;
    }
}