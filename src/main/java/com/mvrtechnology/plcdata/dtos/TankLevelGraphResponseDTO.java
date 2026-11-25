package com.mvrtechnology.plcdata.dtos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TankLevelGraphResponseDTO {

    private Integer plantId;

    private LocalDateTime fromDateTime;

    private LocalDateTime toDateTime;

    private Integer intervalMinutes;

    private String aggregationType;

    private Double sludgeTankCapacity;

    private Long totalRecords;

    private List<TankLevelGraphPointDTO> data;
}