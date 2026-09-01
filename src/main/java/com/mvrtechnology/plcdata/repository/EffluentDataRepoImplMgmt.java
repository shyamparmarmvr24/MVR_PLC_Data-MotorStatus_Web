package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.*;
import com.mvrtechnology.plcdata.entity.EffluentData;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.projection.AllPlantEffluentProjection;
import com.mvrtechnology.plcdata.projection.EffluentAverageProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
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
            if (record.getFlow() != null)
            {
                record.setFlow(record.getFlow().multiply(BigDecimal.valueOf(1000)));
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

            BigDecimal lastFlow = null;

            for (int i = data.size() - 1; i >= 0; i--)
            {
                BigDecimal flow = data.get(i).getCumulativeFlow();

                if (flow != null && flow.compareTo(BigDecimal.ZERO) > 0)
                {
                    lastFlow = flow;
                    break;
                }
            }

            if (firstFlow != null && lastFlow != null)
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
    }

    @Override
    public List<DateAvailabilityDTO> getMonthAvailability(Integer plantId,
                                                          int year,
                                                          int month)
    {
        List<LocalDate> availableDates =
                effluentRepo.getAvailableDates(plantId, year, month);

        Set<LocalDate> availableSet =
                new HashSet<>(availableDates);

        YearMonth selectedMonth = YearMonth.of(year, month);
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        if (selectedMonth.isAfter(currentMonth))
        {
            return new ArrayList<>();
        }

        int lastDay = selectedMonth.equals(currentMonth)
                ? today.getDayOfMonth()
                : selectedMonth.lengthOfMonth();

        List<DateAvailabilityDTO> result = new ArrayList<>();

        for (int day = 1; day <= lastDay; day++)
        {
            LocalDate date = selectedMonth.atDay(day);

            result.add(new DateAvailabilityDTO(
                    date,
                    availableSet.contains(date)
            ));
        }

        return result;
    }

    @Override
    public List<EffluentCumulativeFlowDTO> getDayWiseCumulativeFlow(
            Integer plantId,
            LocalDate date,
            LocalTime fromTime,
            LocalTime toTime)
    {

        LocalDateTime fromDateTime =
                LocalDateTime.of(date,fromTime);

        LocalDateTime toDateTime =
                LocalDateTime.of(date,toTime);

        List<Object[]> rows =
                effluentRepo.getDayWiseCumulativeFlow(
                        plantId,
                        fromDateTime,
                        toDateTime);

        List<EffluentCumulativeFlowDTO> result =
                new ArrayList<>();

        for(Object[] row:rows){

            EffluentCumulativeFlowDTO dto =
                    new EffluentCumulativeFlowDTO();

            dto.setDateAndTime(
                    ((java.sql.Timestamp)row[0]).toLocalDateTime());

            BigDecimal flow=(BigDecimal)row[1];

            if(flow!=null){

                flow=flow.multiply(BigDecimal.valueOf(1000));

            }

            dto.setCumulativeFlow(flow);

            result.add(dto);

        }

        return result;

    }

    @Override
    public EffluentAverageDTO getAverageByPlantAndDate(Integer plantId, LocalDate operationDate)
    {

        EffluentAverageProjection projection =
                effluentRepo.getAverageByPlantAndDate(
                        plantId,
                        operationDate);

        EffluentAverageDTO dto = new EffluentAverageDTO();

        dto.setPlantId(plantId);
        dto.setOperationDate(operationDate);

        if(projection == null)
        {
            dto.setAvgPH(BigDecimal.ZERO);
            dto.setAvgCod(BigDecimal.ZERO);
            dto.setAvgBod(BigDecimal.ZERO);
            dto.setAvgTss(BigDecimal.ZERO);
            dto.setAvgTemperature(BigDecimal.ZERO);
            dto.setAvgTN(BigDecimal.ZERO);
            dto.setAvgFlow(BigDecimal.ZERO);
            dto.setAvgVelocity(BigDecimal.ZERO);
            dto.setCumulativeFlow(BigDecimal.ZERO);

            return dto;
        }

        dto.setAvgPH(projection.getAvgPH());
        dto.setAvgCod(projection.getAvgCOD());
        dto.setAvgBod(projection.getAvgBOD());
        dto.setAvgTss(projection.getAvgTSS());
        dto.setAvgTemperature(projection.getAvgTemperature());
        dto.setAvgTN(projection.getAvgTN());

        dto.setAvgFlow(
                projection.getAvgFlow().multiply(BigDecimal.valueOf(1000)));

        dto.setAvgVelocity(
                projection.getAvgVelocity());

        dto.setCumulativeFlow(
                projection.getCumulativeFlow().multiply(BigDecimal.valueOf(1000)));

        return dto;
    }

    @Override
    public List<AllPlantEffluentResponse> getAllPlantsAverageByDate(LocalDate operationDate)
    {

        List<AllPlantEffluentProjection> projections = effluentRepo.getAllPlantsAverageByDate(operationDate);

        return projections.stream()
                .map(projection -> {

                    AllPlantEffluentResponse dto =
                            new AllPlantEffluentResponse();

                    dto.setPlantId(projection.getPlantId());

                    dto.setPlantName(projection.getPlantName());

                    dto.setAvgPH(projection.getAvgPH());

                    dto.setAvgCOD(projection.getAvgCOD());

                    dto.setAvgBOD(projection.getAvgBOD());

                    dto.setAvgTSS(projection.getAvgTSS());

                    dto.setAvgTemperature(
                            projection.getAvgTemperature()
                    );

                    dto.setAvgTN(projection.getAvgTN());

                    dto.setAvgFlow(
                            projection.getAvgFlow() != null
                                    ? projection.getAvgFlow() * 1000
                                    : 0D
                    );

                    dto.setAvgVelocity(
                            projection.getAvgVelocity()
                    );


                    dto.setCumulativeFlow(
                            projection.getCumulativeFlow() != null
                                    ? projection.getCumulativeFlow() * 1000
                                    : 0D
                    );

                    return dto;
                }).toList();
    }

    private EffluentDailyAverageDTO createEmptyDto(PlantDetails plant, LocalDate date)
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