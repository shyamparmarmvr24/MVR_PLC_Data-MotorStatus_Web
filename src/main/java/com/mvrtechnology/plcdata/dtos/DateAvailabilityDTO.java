package com.mvrtechnology.plcdata.dtos;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateAvailabilityDTO
{
    private LocalDate date;
    private boolean hasData;
}