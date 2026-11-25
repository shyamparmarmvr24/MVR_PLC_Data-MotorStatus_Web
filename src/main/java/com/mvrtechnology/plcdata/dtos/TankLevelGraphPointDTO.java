package com.mvrtechnology.plcdata.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TankLevelGraphPointDTO {

    private LocalDateTime dateTime;

    private Double tankLevel;

    private Long recordCount;
}