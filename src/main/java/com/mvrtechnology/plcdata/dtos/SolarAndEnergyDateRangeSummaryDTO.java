package com.mvrtechnology.plcdata.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolarAndEnergyDateRangeSummaryDTO
{
    private LocalDate operationDate;

    private BigDecimal dailyActProduction;

    private BigDecimal activeEnergy;

    private BigDecimal activeEnergyDay;

    private BigDecimal activeEnergyNight;
}
