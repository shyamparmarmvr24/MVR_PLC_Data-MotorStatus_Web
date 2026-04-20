package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.dtos.PlantEffluentResponseDTO;

public interface IEffluentDataRepoImpl
{
    public PlantEffluentResponseDTO getLatestByPlant(Integer plantId);
}
