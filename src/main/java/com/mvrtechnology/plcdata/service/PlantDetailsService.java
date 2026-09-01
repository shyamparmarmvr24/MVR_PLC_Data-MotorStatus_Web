package com.mvrtechnology.plcdata.service;
import com.mvrtechnology.plcdata.dtos.PlantDetailsResponseDTO;
import com.mvrtechnology.plcdata.repository.IPlantDetailsRepoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlantDetailsService implements IPlantDetailsService
{
    @Autowired
    private IPlantDetailsRepoImpl plantRepo;

    @Override
    public PlantDetailsResponseDTO getPlantDetails(Integer plantId) {
        return plantRepo.getPlantDetails(plantId);
    }
}
