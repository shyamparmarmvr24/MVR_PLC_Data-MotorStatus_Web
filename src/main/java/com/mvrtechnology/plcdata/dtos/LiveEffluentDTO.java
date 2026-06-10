package com.mvrtechnology.plcdata.dtos;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LiveEffluentDTO
{
    private Integer plantId;

    private BigDecimal pH;
    private BigDecimal cod;
    private BigDecimal bod;
    private BigDecimal tss;
    private BigDecimal temperature;
    private BigDecimal tN;
    private BigDecimal flow;
    private BigDecimal velocity;
    private BigDecimal cumulativeFlow;

    private Boolean filterFeedPumpStatus;

    private LocalDateTime timeStamp;
}