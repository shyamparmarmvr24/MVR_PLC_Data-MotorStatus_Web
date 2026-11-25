package com.mvrtechnology.plcdata.service;

import com.mvrtechnology.plcdata.dtos.TankLevelGraphResponseDTO;
import com.mvrtechnology.plcdata.dtos.TankLevelLatestResponseDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.entity.TankLevelData;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

import java.time.LocalDateTime;

public interface ITankLevelDataService
{
    TankLevelData fetchAndSaveTankLevel(
            PlantDetails plant,
            TCPMasterConnection connection
    );

    TankLevelGraphResponseDTO getTankLevelGraph(
            Integer plantId,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime,
            Integer intervalMinutes,
            String aggregationType
    );

    TankLevelLatestResponseDTO getLatestTankLevel(Integer plantId);
}