package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.EffluentDailyAverageDTO;
import com.mvrtechnology.plcdata.dtos.EffluentDayDataDTO;
import com.mvrtechnology.plcdata.dtos.PlantEffluentResponseDTO;
import com.mvrtechnology.plcdata.entity.EffluentData;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public class EffluentDataRepoImplMgmt implements IEffluentDataRepoImpl
{
    @Autowired
    private IEffluentDataRepo effluentRepo;

    @Autowired
    private PlantCache plantCache;

    @Override
    public PlantEffluentResponseDTO getLatestByPlant(Integer plantId)
    {
        PlantDetails plant = plantCache.get(plantId);

        if (plant == null) {
            throw new RuntimeException("Plant not found");
        }

        PlantEffluentResponseDTO dto = new PlantEffluentResponseDTO();

        dto.setPlantId(plant.getPlantId());
        dto.setPlantName(plant.getPlantName());
        dto.setZone(plant.getZone());

        EffluentData latest = effluentRepo.getLatest(plantId).orElse(null);

        if(latest!=null) {
            latest.setCumulativeFlow(latest.getCumulativeFlow().multiply(BigDecimal.valueOf(1000)));
        }

        dto.setEffluentData(latest);

        return dto;
    }

    @Override
    public EffluentDayDataDTO getDayData(Integer plantId, LocalDate date, LocalTime fromTime, LocalTime toTime)
    {
        PlantDetails plant = plantCache.get(plantId);

        if (plant == null)
        {
            throw new RuntimeException("Plant Not Found");
        }

        LocalDateTime fromDateTime = LocalDateTime.of(date, fromTime);

        LocalDateTime toDateTime = LocalDateTime.of(date, toTime);

        List<EffluentData> data = effluentRepo.getDataBetweenTime(plantId, fromDateTime, toDateTime);

        data.forEach(record -> {
            if (record.getCumulativeFlow() != null)
            {
                record.setCumulativeFlow(record.getCumulativeFlow().multiply(BigDecimal.valueOf(1000)));
            }
        });

        EffluentDayDataDTO dto = new EffluentDayDataDTO();

        dto.setPlantId(plantId);
        dto.setPlantName(plant.getPlantName());

        dto.setFromDateTime(fromDateTime);
        dto.setToDateTime(toDateTime);

        dto.setData(data);

        return dto;
    }

    @Override
    public List<EffluentDailyAverageDTO> getDateRangeAverage(Integer plantId, LocalDate fromDate, LocalDate toDate)
    {
        PlantDetails plant = plantCache.get(plantId);

        List<Object[]> rows = effluentRepo.getDateRangeAverage(plantId, fromDate, toDate);

        return rows.stream()
                .map(r -> {

                    EffluentDailyAverageDTO dto = new EffluentDailyAverageDTO();

                    dto.setPlantId(plant.getPlantId());
                    dto.setPlantName(plant.getPlantName());
                    dto.setZone(plant.getZone());
                    dto.setOperationDate(((Date)r[0]).toLocalDate());

                    dto.setAvgPH((BigDecimal) r[1]);
                    dto.setAvgCod((BigDecimal) r[2]);
                    dto.setAvgBod((BigDecimal) r[3]);
                    dto.setAvgTss((BigDecimal) r[4]);
                    dto.setAvgTemperature((BigDecimal) r[5]);
                    dto.setAvgTN((BigDecimal) r[6]);
                    dto.setAvgFlow(((BigDecimal) r[7]).multiply(BigDecimal.valueOf(1000)));
                    dto.setAvgVelocity((BigDecimal) r[8]);
                    BigDecimal firstFlow = (BigDecimal) r[9];
                    BigDecimal lastFlow  = (BigDecimal) r[10];

                    BigDecimal cumulativeFlow = BigDecimal.ZERO;

                    if(firstFlow != null && lastFlow != null)
                    {
                        cumulativeFlow =
                                lastFlow.subtract(firstFlow)
                                        .multiply(BigDecimal.valueOf(1000));
                    }

                    dto.setAvgCumulativeFlow(cumulativeFlow);

                    return dto;
                }).toList();
    }

}
