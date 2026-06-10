package com.mvrtechnology.plcdata.dtos;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AllPlantStatusDTO
{
    private List<PlantInfoDto> plants;
}