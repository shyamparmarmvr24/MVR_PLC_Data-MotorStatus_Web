package com.mvrtechnology.plcdata.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlantSolarResponseDTO
{
    private Integer plantId;
    private String plantName;
    private Integer zone;

    private SolarDataResponseDTO solarData;
}