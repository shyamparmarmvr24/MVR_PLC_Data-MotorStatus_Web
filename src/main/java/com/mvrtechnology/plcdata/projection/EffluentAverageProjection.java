package com.mvrtechnology.plcdata.projection;

import java.math.BigDecimal;

public interface EffluentAverageProjection
{
    BigDecimal getAvgPH();

    BigDecimal getAvgCOD();

    BigDecimal getAvgBOD();

    BigDecimal getAvgTSS();

    BigDecimal getAvgTemperature();

    BigDecimal getAvgTN();

    BigDecimal getAvgFlow();

    BigDecimal getAvgVelocity();

    BigDecimal getCumulativeFlow();
}