package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.dtos.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IEffluentDataRepoImpl
{
    PlantEffluentResponseDTO getLatestByPlant(Integer plantId);
    EffluentDayDataDTO getDayData(Integer plantId, LocalDate date, LocalTime fromTime, LocalTime toTime);
    List<EffluentDailyAverageDTO> getDateRangeAverage(Integer plantId, LocalDate fromDate, LocalDate toDate);
    List<DateAvailabilityDTO> getMonthAvailability(Integer plantId, int year, int month);
    List<EffluentCumulativeFlowDTO> getDayWiseCumulativeFlow(Integer plantId, LocalDate date, LocalTime fromTime, LocalTime toTime);
}
