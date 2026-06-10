package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.dtos.SolarAndEnergyDateRangeSummaryDTO;
import com.mvrtechnology.plcdata.dtos.SolarDayWiseResponseDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ISolarAndEnergyDataRepoImpl
{
    SolarDayWiseResponseDTO getHistoryData(Integer plantId, LocalDate date, LocalTime fromTime, LocalTime toTime);
    List<SolarAndEnergyDateRangeSummaryDTO> getSolarSummary(Integer plantId, LocalDate fromDate, LocalDate toDate);
}