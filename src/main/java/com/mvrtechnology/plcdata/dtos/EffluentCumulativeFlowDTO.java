package com.mvrtechnology.plcdata.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EffluentCumulativeFlowDTO
{
    private LocalDateTime dateAndTime;

    private BigDecimal cumulativeFlow;

}