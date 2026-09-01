package com.mvrtechnology.plcdata.projection;

public interface AllPlantEffluentProjection
{

    Integer getPlantId();

    String getPlantName();

    Double getAvgPH();

    Double getAvgCOD();

    Double getAvgBOD();

    Double getAvgTSS();

    Double getAvgTemperature();

    Double getAvgTN();

    Double getAvgFlow();

    Double getAvgVelocity();

    Double getCumulativeFlow();
}