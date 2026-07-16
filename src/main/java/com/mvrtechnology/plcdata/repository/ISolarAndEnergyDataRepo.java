package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.dtos.SolarDataResponseDTO;
import com.mvrtechnology.plcdata.entity.SolarAndEnergyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ISolarAndEnergyDataRepo extends JpaRepository<SolarAndEnergyData,Long>
{
    @Query(value = """
            SELECT TOP 1 *
            FROM Solar_And_Energy_Data
            WHERE Plant_Id = :plantId
            ORDER BY Date_And_Time_Of_Solar_And_Energy DESC
            """, nativeQuery = true)
    SolarAndEnergyData getLatestData(Integer plantId);

    @Query("""
    SELECT new com.mvrtechnology.plcdata.dtos.SolarDataResponseDTO(
    
    s.operationID,
    s.dailyActProduction,
    s.totalPower,
    s.inputPower,
    s.inverterDcCurrent,
    s.inverterDcVoltage,
    s.inverterCCurrent,
    s.inverterBCurrent,
    s.inverterACurrent,
    s.inverterABVoltage,
    s.inverterBCVoltage,
    s.inverterCAVoltage,
    s.rPhaseCurrent,
    s.yPhaseCurrent,
    s.bPhaseCurrent,
    s.frequency,
    s.powerFactor,
    s.activeEnergy,
    s.dateAndTimeOfSolarAndEnergy,
    s.operationDate
    
    )
    
    FROM SolarAndEnergyData s
    
    WHERE s.plantDetails.plantId=:plantId
    
    AND s.dateAndTimeOfSolarAndEnergy
    BETWEEN :fromDateTime AND :toDateTime
    
    ORDER BY s.dateAndTimeOfSolarAndEnergy
    """)
    List<SolarDataResponseDTO> getHistoryData(Integer plantId, LocalDateTime fromDateTime, LocalDateTime toDateTime);

    @Query("""
    SELECT s
    FROM SolarAndEnergyData s
    WHERE s.plantDetails.plantId = :plantId
    AND s.operationDate BETWEEN :fromDate AND :toDate
    ORDER BY s.operationDate,
    s.dateAndTimeOfSolarAndEnergy
    """)
    List<SolarAndEnergyData> getSummaryData(Integer plantId, LocalDate fromDate, LocalDate toDate);

}

//changes 16/07/26