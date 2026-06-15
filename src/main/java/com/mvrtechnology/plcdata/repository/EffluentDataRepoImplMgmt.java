package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.EffluentDailyAverageDTO;
import com.mvrtechnology.plcdata.dtos.EffluentDayDataDTO;
import com.mvrtechnology.plcdata.dtos.EffluentDaySummaryDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        EffluentDaySummaryDTO summary = new EffluentDaySummaryDTO();

        if (!data.isEmpty())
        {
            summary.setAvgPH(average(data, EffluentData::getPH));

            summary.setAvgCod(average(data, EffluentData::getCod));

            summary.setAvgBod(average(data, EffluentData::getBod));

            summary.setAvgTss(average(data, EffluentData::getTss));

            summary.setAvgTemperature(average(data, EffluentData::getTemperature));

            summary.setAvgTN(average(data, EffluentData::getTN));

            summary.setAvgFlow(average(data, EffluentData::getFlow));

            summary.setAvgVelocity(average(data, EffluentData::getVelocity));

            BigDecimal firstFlow = data.get(0).getCumulativeFlow();

            BigDecimal lastFlow = data.get(data.size() - 1).getCumulativeFlow();

            if(firstFlow != null && lastFlow != null)
            {
                summary.setAvgCumulativeFlow(lastFlow.subtract(firstFlow));
            }
            else
            {
                summary.setAvgCumulativeFlow(BigDecimal.ZERO);
            }
        }

        EffluentDayDataDTO dto = new EffluentDayDataDTO();

        dto.setPlantId(plantId);
        dto.setPlantName(plant.getPlantName());

        dto.setFromDateTime(fromDateTime);
        dto.setToDateTime(toDateTime);

        dto.setData(data);

        dto.setSummary(summary);

        return dto;
    }

    @Override
    public List<EffluentDailyAverageDTO> getDateRangeAverage(Integer plantId, LocalDate fromDate, LocalDate toDate)
    {
        PlantDetails plant = plantCache.get(plantId);

        List<Object[]> rows = effluentRepo.getDateRangeAverage(plantId, fromDate, toDate);

        List<EffluentDailyAverageDTO> dbData =
                rows.stream()
                        .map(r -> {

                            EffluentDailyAverageDTO dto = new EffluentDailyAverageDTO();

                            dto.setPlantId(plant.getPlantId());
                            dto.setPlantName(plant.getPlantName());
                            dto.setZone(plant.getZone());
                            dto.setOperationDate(((Date) r[0]).toLocalDate());

                            dto.setAvgPH((BigDecimal) r[1]);
                            dto.setAvgCod((BigDecimal) r[2]);
                            dto.setAvgBod((BigDecimal) r[3]);
                            dto.setAvgTss((BigDecimal) r[4]);
                            dto.setAvgTemperature((BigDecimal) r[5]);
                            dto.setAvgTN((BigDecimal) r[6]);
                            dto.setAvgFlow(((BigDecimal) r[7]).multiply(BigDecimal.valueOf(1000)));

                            dto.setAvgVelocity((BigDecimal) r[8]);

                            BigDecimal firstFlow = (BigDecimal) r[9];

                            BigDecimal lastFlow =
                                    (BigDecimal) r[10];

                            BigDecimal cumulativeFlow =
                                    BigDecimal.ZERO;

                            if (firstFlow != null && lastFlow != null)
                            {
                                cumulativeFlow = lastFlow.subtract(firstFlow).multiply(BigDecimal.valueOf(1000));
                            }

                            dto.setAvgCumulativeFlow(cumulativeFlow);

                            return dto;
                        }).toList();

        Map<LocalDate, EffluentDailyAverageDTO> dataMap =
                dbData.stream()
                        .collect(Collectors.toMap(
                                EffluentDailyAverageDTO::getOperationDate,
                                dto -> dto));

        List<EffluentDailyAverageDTO> result =
                new ArrayList<>();

        for (LocalDate date = fromDate;
             !date.isAfter(toDate);
             date = date.plusDays(1))
        {
            result.add(
                    dataMap.getOrDefault(
                            date,
                            createEmptyDto(
                                    plant,
                                    date)));
        }

        return result;

//        return rows.stream()
//                .map(r -> {
//
//                    EffluentDailyAverageDTO dto = new EffluentDailyAverageDTO();
//
//                    dto.setPlantId(plant.getPlantId());
//                    dto.setPlantName(plant.getPlantName());
//                    dto.setZone(plant.getZone());
//                    dto.setOperationDate(((Date)r[0]).toLocalDate());
//
//                    dto.setAvgPH((BigDecimal) r[1]);
//                    dto.setAvgCod((BigDecimal) r[2]);
//                    dto.setAvgBod((BigDecimal) r[3]);
//                    dto.setAvgTss((BigDecimal) r[4]);
//                    dto.setAvgTemperature((BigDecimal) r[5]);
//                    dto.setAvgTN((BigDecimal) r[6]);
//                    dto.setAvgFlow(((BigDecimal) r[7]).multiply(BigDecimal.valueOf(1000)));
//                    dto.setAvgVelocity((BigDecimal) r[8]);
//                    BigDecimal firstFlow = (BigDecimal) r[9];
//                    BigDecimal lastFlow  = (BigDecimal) r[10];
//
//                    BigDecimal cumulativeFlow = BigDecimal.ZERO;
//
//                    if(firstFlow != null && lastFlow != null)
//                    {
//                        cumulativeFlow =
//                                lastFlow.subtract(firstFlow)
//                                        .multiply(BigDecimal.valueOf(1000));
//                    }
//
//                    dto.setAvgCumulativeFlow(cumulativeFlow);
//
//                    return dto;
//                }).toList();
    }

    private EffluentDailyAverageDTO createEmptyDto(
            PlantDetails plant,
            LocalDate date)
    {
        EffluentDailyAverageDTO dto =
                new EffluentDailyAverageDTO();

        dto.setPlantId(plant.getPlantId());
        dto.setPlantName(plant.getPlantName());
        dto.setZone(plant.getZone());

        dto.setOperationDate(date);

        return dto;
    }

    private BigDecimal average(List<EffluentData> data, java.util.function.Function<EffluentData, BigDecimal> mapper)
    {
        List<BigDecimal> values = data.stream().map(mapper).filter(java.util.Objects::nonNull).toList();

        if(values.isEmpty())
        {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(values.size()), 2, java.math.RoundingMode.HALF_UP);
    }

}
