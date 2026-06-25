package com.mvrtechnology.plcdata.dtos;
import lombok.Data;

@Data
public class PlantMotorResponseDTO {

    private Integer plantId;
    private String plantName;
    private Integer zone;
    private Boolean plcOnline;
    private MotorStatusDTO motorStatus;
}
