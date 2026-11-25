package com.mvrtechnology.plcdata.projection;
import java.time.LocalDateTime;

public interface TankLevelRawProjection {

    Long getTankLevelOperationID();

    LocalDateTime getDateAndTimeOfTankLevel();

    Double getTankLevel();

    Double getSludgeTankCapacity();
}
