package com.mvrtechnology.plcdata.dtos;
import com.mvrtechnology.plcdata.entity.EffluentData;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EffluentDayDataDTO
{
    private Integer plantId;
    private String plantName;
    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;
    private List<EffluentData> data;
}
