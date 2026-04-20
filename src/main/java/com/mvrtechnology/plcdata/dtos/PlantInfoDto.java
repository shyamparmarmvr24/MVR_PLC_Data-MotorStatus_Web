package com.mvrtechnology.plcdata.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlantInfoDto
{
    private Integer plantId;
    private String plantName;
    private Integer zone;
    private Boolean plcStatusSM400;
}
