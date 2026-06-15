package com.mvrtechnology.plcdata.dtos;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class EffluentDaySummaryDTO
{
    private BigDecimal avgPH;
    private BigDecimal avgCod;
    private BigDecimal avgBod;
    private BigDecimal avgTss;
    private BigDecimal avgTemperature;
    private BigDecimal avgTN;
    private BigDecimal avgFlow;
    private BigDecimal avgVelocity;
    private BigDecimal avgCumulativeFlow;
}