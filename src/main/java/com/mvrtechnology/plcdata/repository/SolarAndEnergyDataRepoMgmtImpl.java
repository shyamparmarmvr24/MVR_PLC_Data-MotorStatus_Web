package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.*;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SolarAndEnergyDataRepoMgmtImpl implements ISolarAndEnergyDataRepoImpl
{
    @Autowired
    private ISolarAndEnergyDataRepo solarAndEnergyRepo;

    @Autowired
    private IPlantDetailsRepo plantRepo;

    @Autowired
    private PlantCache plantDet;

    @Override
    public SolarDayWiseResponseDTO getHistoryData(Integer plantId, LocalDate date, LocalTime fromTime, LocalTime toTime)
    {
        PlantDetails plant = plantDet.get(plantId);

        LocalDateTime fromDateTime = LocalDateTime.of(date, fromTime);

        LocalDateTime toDateTime = LocalDateTime.of(date, toTime);

        List<SolarDataResponseDTO> history =
                solarAndEnergyRepo.getHistoryData(
                        plantId,
                        fromDateTime,
                        toDateTime
                );

        if(history == null || history.isEmpty())
        {
            SolarDayWiseResponseDTO response =
                    new SolarDayWiseResponseDTO();

            response.setPlantId(plantId);
            response.setPlantName(plant.getPlantName());

            response.setDate(date);
            response.setFromTime(fromTime);
            response.setToTime(toTime);

            response.setData(null);

            return response;
        }

//        BigDecimal previousActiveEnergy = null;
//
//        for (SolarDataResponseDTO dto : history)
//        {
//            dto.setActiveEnergyConsumption(
//                    calculateActiveEnergyConsumption(
//                            dto.getActiveEnergy(),
//                            previousActiveEnergy));
//
//            previousActiveEnergy =
//                    dto.getActiveEnergy();
//        }
        BigDecimal previousActiveEnergy = null;

//        BigDecimal firstActiveEnergy = history.get(0).getActiveEnergy();

        BigDecimal firstActiveEnergy = history.stream()
                .map(SolarDataResponseDTO::getActiveEnergy)
                .filter(energy ->
                        energy != null &&
                                energy.compareTo(BigDecimal.ZERO) != 0
                )
                .findFirst()
                .orElse(null);

        for (SolarDataResponseDTO dto : history)
        {

            dto.setActiveEnergyConsumption(
                    calculateActiveEnergyConsumption(
                            dto.getActiveEnergy(),
                            previousActiveEnergy));

            previousActiveEnergy = dto.getActiveEnergy();

//            if (firstActiveEnergy == null || dto.getActiveEnergy() == null)
//            {
//                dto.setActiveEnergyFromStart(BigDecimal.ZERO);
//            }
//            else
//            {
//                BigDecimal difference = dto.getActiveEnergy().subtract(firstActiveEnergy);
//
//                if (difference.compareTo(BigDecimal.ZERO) < 0)
//                {
//                    difference = BigDecimal.ZERO;
//                }
//
//                dto.setActiveEnergyFromStart(difference);
//            }
            if (firstActiveEnergy == null ||
                    dto.getActiveEnergy() == null ||
                    dto.getActiveEnergy().compareTo(BigDecimal.ZERO) == 0)
            {
                dto.setActiveEnergyFromStart(BigDecimal.ZERO);
            }
            else
            {
                BigDecimal difference =
                        dto.getActiveEnergy().subtract(firstActiveEnergy);

                if (difference.compareTo(BigDecimal.ZERO) < 0)
                {
                    difference = BigDecimal.ZERO;
                }

                dto.setActiveEnergyFromStart(difference);
            }
        }

        SolarDayWiseResponseDTO response = new SolarDayWiseResponseDTO();

        response.setPlantId(plantId);
        response.setPlantName(plant.getPlantName());

        response.setDate(date);
        response.setFromTime(fromTime);
        response.setToTime(toTime);

        response.setData(history);

        return response;
    }

    @Override
    public List<SolarAndEnergyDateRangeSummaryDTO> getSolarSummary(Integer plantId, LocalDate fromDate, LocalDate toDate)
    {
        List<SolarSummaryDataDTO> records =
                solarAndEnergyRepo.getSummaryData(
                        plantId,
                        fromDate,
                        toDate);

        if(records == null || records.isEmpty())
        {
            return null;
        }

        Map<LocalDate,List<SolarSummaryDataDTO>> dayMap =
                records.stream()
                        .collect(Collectors.groupingBy(
                                SolarSummaryDataDTO::getOperationDate,
                                LinkedHashMap::new,
                                Collectors.toList()));

        List<SolarAndEnergyDateRangeSummaryDTO> result =
                new ArrayList<>();

        LocalDate currentDate = fromDate;

        while (!currentDate.isAfter(toDate))
        {
            List<SolarSummaryDataDTO> dayRecords =
                    dayMap.get(currentDate);

            if(dayRecords == null || dayRecords.isEmpty())
            {
                SolarAndEnergyDateRangeSummaryDTO dto =
                        new SolarAndEnergyDateRangeSummaryDTO();

                dto.setOperationDate(currentDate);

                dto.setDailyActProduction(null);
                dto.setActiveEnergy(null);
                dto.setActiveEnergyDay(null);
                dto.setActiveEnergyNight(null);

                result.add(dto);

                currentDate = currentDate.plusDays(1);

                continue;
            }

//            SolarSummaryDataDTO first =
//                    dayRecords.get(0);
//
//            SolarSummaryDataDTO last =
//                    dayRecords.get(dayRecords.size()-1);

            SolarSummaryDataDTO first = dayRecords.stream()
                    .filter(record ->
                            record.getActiveEnergy() != null &&
                                    record.getActiveEnergy().compareTo(BigDecimal.ZERO) != 0
                    )
                    .findFirst()
                    .orElse(null);

            SolarSummaryDataDTO last = null;

            for (int i = dayRecords.size() - 1; i >= 0; i--)
            {
                SolarSummaryDataDTO record = dayRecords.get(i);

                if (record.getActiveEnergy() != null &&
                        record.getActiveEnergy().compareTo(BigDecimal.ZERO) != 0)
                {
                    last = record;
                    break;
                }
            }

            BigDecimal production =
                    dayRecords.stream()
                            .map(SolarSummaryDataDTO::getDailyActProduction)
                            .filter(Objects::nonNull)
                            .max(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

            BigDecimal energy6AM = null;
            BigDecimal energy6PM = null;

            long min6Diff = Long.MAX_VALUE;
            long min18Diff = Long.MAX_VALUE;

//            for(SolarSummaryDataDTO record : dayRecords)
//            {
//                LocalTime time =
//                        record.getDateAndTimeOfSolarAndEnergy()
//                                .toLocalTime();
//
//                long diff6 =
//                        Math.abs(Duration.between(
//                                LocalTime.of(6,0),
//                                time).toMinutes());
//
//                if(diff6 < min6Diff)
//                {
//                    min6Diff = diff6;
//                    energy6AM = record.getActiveEnergy();
//                }
//
//                long diff18 =
//                        Math.abs(Duration.between(
//                                LocalTime.of(18,0),
//                                time).toMinutes());
//
//                if(diff18 < min18Diff)
//                {
//                    min18Diff = diff18;
//                    energy6PM = record.getActiveEnergy();
//                }
//            }

            for (SolarSummaryDataDTO record : dayRecords)
            {
                /*
                 * Ignore NULL and ZERO active energy readings.
                 */
                if (record.getActiveEnergy() == null ||
                        record.getActiveEnergy().compareTo(BigDecimal.ZERO) == 0)
                {
                    continue;
                }

                if (record.getDateAndTimeOfSolarAndEnergy() == null)
                {
                    continue;
                }

                LocalTime time =
                        record.getDateAndTimeOfSolarAndEnergy()
                                .toLocalTime();

                /*
                 * Find nearest reading to 06:00 AM
                 */
                long diff6 =
                        Math.abs(Duration.between(
                                LocalTime.of(6, 0),
                                time
                        ).toMinutes());

                if (diff6 < min6Diff)
                {
                    min6Diff = diff6;
                    energy6AM = record.getActiveEnergy();
                }

                /*
                 * Find nearest reading to 06:00 PM
                 */
                long diff18 =
                        Math.abs(Duration.between(
                                LocalTime.of(18, 0),
                                time
                        ).toMinutes());

                if (diff18 < min18Diff)
                {
                    min18Diff = diff18;
                    energy6PM = record.getActiveEnergy();
                }
            }

//            BigDecimal totalEnergy =
//                    last.getActiveEnergy()
//                            .subtract(first.getActiveEnergy());

            BigDecimal totalEnergy = null;

            if (first != null && last != null)
            {
                totalEnergy =
                        last.getActiveEnergy()
                                .subtract(first.getActiveEnergy());
            }

            BigDecimal dayEnergy =
                    energy6PM != null && energy6AM != null
                            ? energy6PM.subtract(energy6AM)
                            : null;

//            BigDecimal nightEnergy =
//                    dayEnergy != null
//                            ? totalEnergy.subtract(dayEnergy)
//                            : null;

            BigDecimal nightEnergy =
                    dayEnergy != null && totalEnergy != null
                            ? totalEnergy.subtract(dayEnergy)
                            : null;

            SolarAndEnergyDateRangeSummaryDTO dto =
                    new SolarAndEnergyDateRangeSummaryDTO();

            dto.setOperationDate(currentDate);
            dto.setDailyActProduction(production);
            dto.setActiveEnergy(totalEnergy);
            dto.setActiveEnergyDay(dayEnergy);
            dto.setActiveEnergyNight(nightEnergy);

            result.add(dto);

            currentDate = currentDate.plusDays(1);
        }

        return result;
    }

    private BigDecimal calculateActiveEnergyConsumption(BigDecimal currentEnergy, BigDecimal previousEnergy)
    {
        if (currentEnergy == null || previousEnergy == null)
        {
            return BigDecimal.ZERO;
        }

        BigDecimal consumption =
                currentEnergy.subtract(previousEnergy);

        return consumption.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : consumption;
    }
}