package com.mvrtechnology.plcdata.projection;

import java.time.LocalDateTime;

public interface TankLevelGraphProjection {

    LocalDateTime getBucketTime();

    Double getAverageTankLevel();

    Long getRecordCount();
}