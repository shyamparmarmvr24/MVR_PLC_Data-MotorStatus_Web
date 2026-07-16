package com.mvrtechnology.plcdata.dtos;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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

    public SolarDataResponseDTO(
            Long operationID,
            BigDecimal dailyActProduction,
            BigDecimal totalPower,
            BigDecimal inputPower,
            BigDecimal inverterDcCurrent,
            BigDecimal inverterDcVoltage,
            BigDecimal inverterCCurrent,
            BigDecimal inverterBCurrent,
            BigDecimal inverterACurrent,
            BigDecimal inverterABVoltage,
            BigDecimal inverterBCVoltage,
            BigDecimal inverterCAVoltage,
            BigDecimal rPhaseCurrent,
            BigDecimal yPhaseCurrent,
            BigDecimal bPhaseCurrent,
            BigDecimal frequency,
            BigDecimal powerFactor,
            BigDecimal activeEnergy,
            LocalDateTime dateAndTimeOfSolarAndEnergy,
            LocalDate operationDate
    )
    {
        this.operationID = operationID;
        this.dailyActProduction = dailyActProduction;
        this.totalPower = totalPower;
        this.inputPower = inputPower;
        this.inverterDcCurrent = inverterDcCurrent;
        this.inverterDcVoltage = inverterDcVoltage;
        this.inverterCCurrent = inverterCCurrent;
        this.inverterBCurrent = inverterBCurrent;
        this.inverterACurrent = inverterACurrent;
        this.inverterABVoltage = inverterABVoltage;
        this.inverterBCVoltage = inverterBCVoltage;
        this.inverterCAVoltage = inverterCAVoltage;
        this.rPhaseCurrent = rPhaseCurrent;
        this.yPhaseCurrent = yPhaseCurrent;
        this.bPhaseCurrent = bPhaseCurrent;
        this.frequency = frequency;
        this.powerFactor = powerFactor;
        this.activeEnergy = activeEnergy;
        this.dateAndTimeOfSolarAndEnergy = dateAndTimeOfSolarAndEnergy;
        this.operationDate = operationDate;
    }
}
