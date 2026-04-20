package com.mvrtechnology.plcdata.service;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.dtos.PlantEffluentResponseDTO;
import com.mvrtechnology.plcdata.entity.EffluentData;
import com.mvrtechnology.plcdata.entity.PlantDetails;

public interface IEffluentDataService
{
    public EffluentData fetchAndSaveEffluentData(PlantDetails plant, TCPMasterConnection connection);
    public PlantEffluentResponseDTO getLatestByPlant(Integer plantId);
}
