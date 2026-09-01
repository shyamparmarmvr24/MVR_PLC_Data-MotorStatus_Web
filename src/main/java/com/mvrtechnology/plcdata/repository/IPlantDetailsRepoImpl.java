package com.mvrtechnology.plcdata.repository;

import com.mvrtechnology.plcdata.dtos.PlantDetailsResponseDTO;

public interface IPlantDetailsRepoImpl
{
    PlantDetailsResponseDTO getPlantDetails(Integer plantId);
}
