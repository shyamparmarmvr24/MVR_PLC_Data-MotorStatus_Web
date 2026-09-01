package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.dtos.PlantDetailsResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PlantDetailsRepoMgmtImpl implements IPlantDetailsRepoImpl
{
    @Autowired
    private IPlantDetailsRepo plantDetailsRepository;

    @Override
    public PlantDetailsResponseDTO getPlantDetails(Integer plantId) {

        return plantDetailsRepository.findPlantDetailsByPlantId(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with ID: " + plantId));
    }

}
