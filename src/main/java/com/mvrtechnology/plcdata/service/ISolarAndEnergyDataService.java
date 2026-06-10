package com.mvrtechnology.plcdata.service;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.dtos.PlantSolarResponseDTO;
import com.mvrtechnology.plcdata.dtos.SolarAndEnergyDateRangeSummaryDTO;
import com.mvrtechnology.plcdata.dtos.SolarDayWiseResponseDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.entity.SolarAndEnergyData;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ISolarAndEnergyDataService
{
    SolarAndEnergyData fetchAndSaveSolarAndEnergy(PlantDetails plant, TCPMasterConnection connection);
    PlantSolarResponseDTO getLatestByPlant(Integer plantId);
    SolarDayWiseResponseDTO getHistoryData(Integer plantId, LocalDate date, LocalTime fromTime, LocalTime toTime);
    List<SolarAndEnergyDateRangeSummaryDTO> getSolarSummary(Integer plantId, LocalDate fromDate, LocalDate toDate);
}
