package com.mvrtechnology.plcdata.service;

import com.mvrtechnology.plcdata.dtos.PlantDetailsResponseDTO;

public interface IPlantDetailsService
{
    PlantDetailsResponseDTO getPlantDetails(Integer plantId);
}
