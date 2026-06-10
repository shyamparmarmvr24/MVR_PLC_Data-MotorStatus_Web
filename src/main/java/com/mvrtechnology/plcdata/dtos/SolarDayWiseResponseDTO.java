package com.mvrtechnology.plcdata.dtos;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class SolarDayWiseResponseDTO
{
    private Integer plantId;
    private String plantName;

    private LocalDate date;

    private LocalTime fromTime;
    private LocalTime toTime;

    private List<SolarDataResponseDTO> data;
}
