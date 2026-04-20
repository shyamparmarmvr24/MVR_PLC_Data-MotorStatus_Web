package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.PlantEffluentResponseDTO;
import com.mvrtechnology.plcdata.entity.EffluentData;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EffluentDataRepoImplMgmt implements IEffluentDataRepoImpl
{
    @Autowired
    private IEffluentDataRepo effluentRepo;

    @Autowired
    private PlantCache plantCache;


    @Override
    public PlantEffluentResponseDTO getLatestByPlant(Integer plantId) {

        PlantDetails plant = plantCache.get(plantId);

        if (plant == null) {
            throw new RuntimeException("Plant not found");
        }

        PlantEffluentResponseDTO dto = new PlantEffluentResponseDTO();

        dto.setPlantId(plant.getPlantId());
        dto.setPlantName(plant.getPlantName());
        dto.setZone(plant.getZone());

        EffluentData latest = effluentRepo.findTopByPlantDetails_PlantIdOrderByDateAndTimeOfEffluentDesc(plantId).orElse(null);

        dto.setEffluentData(latest);

        return dto;
    }

}
