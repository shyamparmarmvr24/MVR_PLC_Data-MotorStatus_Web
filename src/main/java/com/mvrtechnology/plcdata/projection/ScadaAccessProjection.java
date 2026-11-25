package com.mvrtechnology.plcdata.projection;

import com.mvrtechnology.plcdata.enums.ScadaAccessType;

public interface ScadaAccessProjection
{
    Long getAccessId();

    String getIpAddress();

    ScadaAccessType getAccessType();

    Integer getPlantId();
}