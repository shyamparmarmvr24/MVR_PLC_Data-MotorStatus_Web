package com.mvrtechnology.plcdata.projection;

import java.math.BigDecimal;

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

    Double getCumulativeFlow();

    BigDecimal getFlowMeterReadingAm();

    BigDecimal getFlowMeterReadingPm();
}