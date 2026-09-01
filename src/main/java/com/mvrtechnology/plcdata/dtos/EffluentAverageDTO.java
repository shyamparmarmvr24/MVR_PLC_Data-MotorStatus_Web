package com.mvrtechnology.plcdata.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EffluentAverageDTO {

    private Integer plantId;

    private LocalDate operationDate;

    private BigDecimal avgPH;

    private BigDecimal avgCod;

    private BigDecimal avgBod;

    private BigDecimal avgTss;

    private BigDecimal avgTemperature;

    private BigDecimal avgTN;

    private BigDecimal avgFlow;

    private BigDecimal avgVelocity;

    private BigDecimal cumulativeFlow;
}