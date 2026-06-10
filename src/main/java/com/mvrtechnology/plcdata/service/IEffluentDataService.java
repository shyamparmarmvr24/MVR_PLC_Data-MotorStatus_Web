package com.mvrtechnology.plcdata.service;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.dtos.EffluentDailyAverageDTO;
import com.mvrtechnology.plcdata.dtos.EffluentDayDataDTO;
import com.mvrtechnology.plcdata.dtos.PlantEffluentResponseDTO;
import com.mvrtechnology.plcdata.entity.EffluentData;
import com.mvrtechnology.plcdata.entity.PlantDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface IEffluentDataService
{
    EffluentData fetchAndSaveEffluentData(PlantDetails plant, TCPMasterConnection connection);
    PlantEffluentResponseDTO getLatestByPlant(Integer plantId);
    EffluentDayDataDTO getDayData(Integer plantId, LocalDate date, LocalTime fromTime, LocalTime toTime);
    List<EffluentDailyAverageDTO> getDateRangeAverage(Integer plantId, LocalDate fromDate, LocalDate toDate);
}
