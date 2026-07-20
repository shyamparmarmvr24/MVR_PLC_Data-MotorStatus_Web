package com.mvrtechnology.plcdata.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SolarSummaryDataDTO
{
    private LocalDate operationDate;

    private BigDecimal dailyActProduction;

    private BigDecimal activeEnergy;

    private LocalDateTime dateAndTimeOfSolarAndEnergy;
}