package com.mvrtechnology.plcdata.service;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.dtos.TankLevelGraphPointDTO;
import com.mvrtechnology.plcdata.dtos.TankLevelGraphResponseDTO;
import com.mvrtechnology.plcdata.dtos.TankLevelLatestResponseDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.entity.TankLevelData;
import com.mvrtechnology.plcdata.exception.BusinessException;
import com.mvrtechnology.plcdata.projection.TankLevelGraphProjection;
import com.mvrtechnology.plcdata.projection.TankLevelLatestProjection;
import com.mvrtechnology.plcdata.projection.TankLevelRawProjection;
import com.mvrtechnology.plcdata.repository.IPlantDetailsRepo;
import com.mvrtechnology.plcdata.repository.ITankLevelDataRepo;
import com.mvrtechnology.plcdata.util.ModbusReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TankLevelDataServiceImpl implements ITankLevelDataService
{
    private static final int TANK_LEVEL_REGISTER = 40552;

    private static final int TANK_CAPACITY = 40548;

    @Autowired
    private ITankLevelDataRepo tankLevelDataRepo;

    @Autowired
    private IPlantDetailsRepo plantDetailsRepository;

    private static final List<Integer> ALLOWED_INTERVALS =
            List.of(1, 15, 30, 45, 60);

    private static final List<String> ALLOWED_AGGREGATION_TYPES =
            List.of("RAW", "AVERAGE");



    @Override
    public TankLevelData fetchAndSaveTankLevel(
            PlantDetails plant,
            TCPMasterConnection connection
    )
    {
        try
        {
            double tankLevel =
                    ModbusReader.readFloat(
                            connection,
                            TANK_LEVEL_REGISTER
                    ) * 1.0;

            double tankCapacity =
                    ModbusReader.readFloat(
                            connection,
                            TANK_CAPACITY
                    ) * 1.0;

            tankLevel = Math.round(tankLevel * 100.0) / 100.0;

            tankCapacity = Math.round(tankCapacity * 100.0) / 100.0;

            if (tankLevel < 0) {
                log.warn(
                        "Skipping tank level data because negative value received. Plant ID: {}, Tank Level: {}",
                        plant.getPlantId(),
                        tankLevel
                );
                return null;
            }

            TankLevelData data = new TankLevelData();

            data.setPlantDetails(plant);

            data.setTankLevel(tankLevel);

            data.setSludgeTankCapacity(tankCapacity);

            data.setDateAndTimeOfTankLevel(
                    LocalDateTime.now()
            );

            data.setOperationDate(
                    LocalDate.now()
            );


            TankLevelData saved =
                    tankLevelDataRepo.save(data);


            log.info(
                    "Tank level saved successfully. Plant ID: {}, Tank Level: {}",
                    plant.getPlantId(),
                    tankLevel
            );


            return saved;
        }
        catch (Exception e)
        {
            log.warn(
                    "Tank level fetch/save failed. Plant ID: {}",
                    plant.getPlantId(),
                    e
            );

            return null;
        }
    }

    @Override
    public TankLevelGraphResponseDTO getTankLevelGraph(
            Integer plantId,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime,
            Integer intervalMinutes,
            String aggregationType
    ) {

        // 1. Validate plant

        if (plantId == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "plantId",
                    "Plant ID is required."
            );
        }

        if (!plantDetailsRepository.existsById(plantId)) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "plantId",
                    "Plant not found."
            );
        }

        // 2. Validate dates

        if (fromDateTime == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "fromDateTime",
                    "From date and time is required."
            );
        }

        if (toDateTime == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "toDateTime",
                    "To date and time is required."
            );
        }

        if (!fromDateTime.isBefore(toDateTime)) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "dateRange",
                    "From date and time must be earlier than To date and time."
            );
        }

        // 3. Validate interval

        if (intervalMinutes == null ||
                !ALLOWED_INTERVALS.contains(intervalMinutes)) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "intervalMinutes",
                    "Interval must be 1, 15, 30, 45 or 60 minutes."
            );
        }

        // 4. Validate aggregation type

        if (aggregationType == null ||
                !ALLOWED_AGGREGATION_TYPES.contains(
                        aggregationType.trim().toUpperCase()
                )) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "aggregationType",
                    "Aggregation type must be RAW or AVERAGE."
            );
        }

        aggregationType = aggregationType.trim().toUpperCase();

        // 5. Fetch capacity only once

        Double tankCapacity = tankLevelDataRepo
                .findTankCapacity(plantId)
                .orElse(null);

        List<TankLevelGraphPointDTO> graphData;

        // ==========================================
        // RAW DATA
        // ==========================================

        if (aggregationType.equals("RAW")) {

            if (intervalMinutes == 1) {

                // Every minute actual records

                List<TankLevelRawProjection> records =
                        tankLevelDataRepo.findRawTankLevelData(
                                plantId,
                                fromDateTime,
                                toDateTime
                        );

                graphData = records.stream()
                        .map(record -> new TankLevelGraphPointDTO(
                                record.getDateAndTimeOfTankLevel(),
                                roundToTwoDecimals(record.getTankLevel()),
                                1L
                        ))
                        .toList();

            } else {

                // Actual reading nearest to each interval timestamp

                List<TankLevelRawProjection> records =
                        tankLevelDataRepo.findRawIntervalData(
                                plantId,
                                fromDateTime,
                                toDateTime,
                                intervalMinutes
                        );

                graphData = records.stream()
                        .map(record -> new TankLevelGraphPointDTO(
                                record.getDateAndTimeOfTankLevel(),
                                roundToTwoDecimals(record.getTankLevel()),
                                1L
                        ))
                        .toList();
            }
        }

        // ==========================================
        // AVERAGE DATA
        // ==========================================

        else {

            graphData = new java.util.ArrayList<>();

            // First point - raw reading

            tankLevelDataRepo.findFirstRawReading(
                    plantId,
                    fromDateTime,
                    intervalMinutes
            ).ifPresent(record -> {

                graphData.add(
                        new TankLevelGraphPointDTO(
                                fromDateTime,
                                roundToTwoDecimals(record.getTankLevel()),
                                1L
                        )
                );

            });

            // Remaining points - interval averages

            List<TankLevelGraphProjection> records =
                    tankLevelDataRepo.findAverageTankLevelData(
                            plantId,
                            fromDateTime,
                            toDateTime,
                            intervalMinutes
                    );

            for (TankLevelGraphProjection record : records) {

                graphData.add(
                        new TankLevelGraphPointDTO(
                                record.getBucketTime(),
                                roundToTwoDecimals(record.getAverageTankLevel()),
                                record.getRecordCount()
                        )
                );
            }
        }

        // 6. Total records

        long totalRecords = graphData.stream()
                .mapToLong(TankLevelGraphPointDTO::getRecordCount)
                .sum();

        // 7. Response

        return new TankLevelGraphResponseDTO(
                plantId,
                fromDateTime,
                toDateTime,
                intervalMinutes,
                aggregationType,
                tankCapacity,
                totalRecords,
                graphData
        );
    }

    @Override
    public TankLevelLatestResponseDTO getLatestTankLevel(Integer plantId) {

        if (plantId == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "plantId",
                    "Plant ID is required."
            );
        }

        TankLevelLatestProjection latest =
                tankLevelDataRepo.findLatestTankLevel(plantId)
                        .orElseThrow(() -> new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "plantId",
                                "No tank level data found for this plant."
                        ));

        return new TankLevelLatestResponseDTO(
                plantId,
                roundToTwoDecimals(latest.getTankLevel()),
                roundToTwoDecimals(latest.getSludgeTankCapacity()),
                latest.getDateAndTimeOfTankLevel()
        );
    }

    private Double roundToTwoDecimals(Double value) {
        if (value == null) {
            return null;
        }

        return Math.round(value * 100.0) / 100.0;
    }
}