package com.mvrtechnology.plcdata.dtos;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SolarDataResponseDTO
{
    private Long operationID;

    private BigDecimal dailyActProduction;

    private BigDecimal totalPower;

    private BigDecimal inputPower;

    private BigDecimal inverterDcCurrent;

    private BigDecimal inverterDcVoltage;

    private BigDecimal inverterCCurrent;

    private BigDecimal inverterBCurrent;

    private BigDecimal inverterACurrent;

    private BigDecimal inverterABVoltage;

    private BigDecimal inverterBCVoltage;

    private BigDecimal inverterCAVoltage;

    private BigDecimal rPhaseCurrent;

    private BigDecimal yPhaseCurrent;

    private BigDecimal bPhaseCurrent;

    private BigDecimal frequency;

    private BigDecimal powerFactor;

    private BigDecimal activeEnergy;

    private BigDecimal activeEnergyConsumption;

    private LocalDateTime dateAndTimeOfSolarAndEnergy;

    private LocalDate operationDate;
}
