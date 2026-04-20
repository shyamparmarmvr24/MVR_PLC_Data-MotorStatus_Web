package com.mvrtechnology.plcdata.dtos;
import com.mvrtechnology.plcdata.entity.EffluentData;
import lombok.Data;

@Data
public class PlantEffluentResponseDTO
{
    private Integer plantId;
    private String plantName;
    private Integer zone;
    private EffluentData effluentData;
}
