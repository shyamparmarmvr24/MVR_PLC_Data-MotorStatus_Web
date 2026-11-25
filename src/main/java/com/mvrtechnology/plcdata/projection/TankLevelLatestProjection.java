package com.mvrtechnology.plcdata.projection;
import java.time.LocalDateTime;

public interface TankLevelLatestProjection {

    Double getTankLevel();

    Double getSludgeTankCapacity();

    LocalDateTime getDateAndTimeOfTankLevel();
}
