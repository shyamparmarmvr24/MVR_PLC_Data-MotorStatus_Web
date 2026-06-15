package com.mvrtechnology.plcdata.dtos;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PlantInfoDto
{
    private Integer plantId;
    private String plantName;
    private Integer zone;
    private Boolean plcStatusSM400;
    private Integer plantKLD;
    private String district;
    private Boolean isMNITCompleted;
    private LocalDate mnitCompletionDate;
    private Boolean isSolarCompleted;
    private LocalDate solarCompletionDate;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
