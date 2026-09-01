package com.mvrtechnology.plcdata.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllPlantEffluentResponse {

    private Integer plantId;

    private String plantName;

    private Double avgPH;

    private Double avgCOD;

    private Double avgBOD;

    private Double avgTSS;

    private Double avgTemperature;

    private Double avgTN;

    private Double avgFlow;

    private Double avgVelocity;

    private Double cumulativeFlow;
}
