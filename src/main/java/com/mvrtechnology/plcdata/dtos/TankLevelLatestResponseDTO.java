package com.mvrtechnology.plcdata.dtos;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TankLevelLatestResponseDTO {

    private Integer plantId;

    private Double tankLevel;

    private Double sludgeTankCapacity;

    private LocalDateTime dateAndTimeOfTankLevel;
}