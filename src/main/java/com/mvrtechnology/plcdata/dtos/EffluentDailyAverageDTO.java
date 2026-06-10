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
public class EffluentDailyAverageDTO
{
    private Integer plantId;
    private String plantName;
    private Integer zone;
    private LocalDate operationDate;
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