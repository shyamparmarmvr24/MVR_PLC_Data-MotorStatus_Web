package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.SolarAndEnergyDateRangeSummaryDTO;
import com.mvrtechnology.plcdata.dtos.SolarDataResponseDTO;
import com.mvrtechnology.plcdata.dtos.SolarDayWiseResponseDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.entity.SolarAndEnergyData;
import org.springframework.beans.BeanUtils;
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

        List<SolarAndEnergyData> history = solarAndEnergyRepo.getHistoryData(plantId, fromDateTime, toDateTime);

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

        List<SolarDataResponseDTO> dtoList = new ArrayList<>();

        BigDecimal previousActiveEnergy = null;

        for (SolarAndEnergyData data : history)
        {
            SolarDataResponseDTO dto = new SolarDataResponseDTO();

            BeanUtils.copyProperties(data, dto);

            BigDecimal currentActiveEnergy = data.getActiveEnergy();

            dto.setActiveEnergyConsumption(calculateActiveEnergyConsumption(currentActiveEnergy, previousActiveEnergy));

            previousActiveEnergy = currentActiveEnergy;

            dtoList.add(dto);
        }

        SolarDayWiseResponseDTO response = new SolarDayWiseResponseDTO();

        response.setPlantId(plantId);
        response.setPlantName(plant.getPlantName());

        response.setDate(date);
        response.setFromTime(fromTime);
        response.setToTime(toTime);

        response.setData(dtoList);

        return response;
    }

    @Override
    public List<SolarAndEnergyDateRangeSummaryDTO> getSolarSummary(Integer plantId, LocalDate fromDate, LocalDate toDate)
    {
        List<SolarAndEnergyData> records = solarAndEnergyRepo.getSummaryData(plantId, fromDate, toDate);

        if(records == null || records.isEmpty())
        {
            return null;
        }

        Map<LocalDate,List<SolarAndEnergyData>> dayMap =
                records.stream()
                        .collect(Collectors.groupingBy(
                                SolarAndEnergyData::getOperationDate,
                                LinkedHashMap::new,
                                Collectors.toList()));

        List<SolarAndEnergyDateRangeSummaryDTO> result =
                new ArrayList<>();

        LocalDate currentDate = fromDate;

        while (!currentDate.isAfter(toDate))
        {
            List<SolarAndEnergyData> dayRecords =
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

            SolarAndEnergyData first =
                    dayRecords.get(0);

            SolarAndEnergyData last =
                    dayRecords.get(dayRecords.size()-1);

            BigDecimal production =
                    dayRecords.stream()
                            .map(SolarAndEnergyData::getDailyActProduction)
                            .filter(Objects::nonNull)
                            .max(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

            BigDecimal energy6AM = null;
            BigDecimal energy6PM = null;

            long min6Diff = Long.MAX_VALUE;
            long min18Diff = Long.MAX_VALUE;

            for(SolarAndEnergyData record : dayRecords)
            {
                LocalTime time =
                        record.getDateAndTimeOfSolarAndEnergy()
                                .toLocalTime();

                long diff6 =
                        Math.abs(Duration.between(
                                LocalTime.of(6,0),
                                time).toMinutes());

                if(diff6 < min6Diff)
                {
                    min6Diff = diff6;
                    energy6AM = record.getActiveEnergy();
                }

                long diff18 =
                        Math.abs(Duration.between(
                                LocalTime.of(18,0),
                                time).toMinutes());

                if(diff18 < min18Diff)
                {
                    min18Diff = diff18;
                    energy6PM = record.getActiveEnergy();
                }
            }

            BigDecimal totalEnergy =
                    last.getActiveEnergy()
                            .subtract(first.getActiveEnergy());

            BigDecimal dayEnergy =
                    energy6PM != null && energy6AM != null
                            ? energy6PM.subtract(energy6AM)
                            : null;

            BigDecimal nightEnergy =
                    dayEnergy != null
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