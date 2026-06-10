package com.mvrtechnology.plcdata.repository;
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
    SELECT s
    FROM SolarAndEnergyData s
    WHERE s.plantDetails.plantId = :plantId
    AND s.dateAndTimeOfSolarAndEnergy >= :fromDateTime
    AND s.dateAndTimeOfSolarAndEnergy <= :toDateTime
    ORDER BY s.dateAndTimeOfSolarAndEnergy
    """)
    List<SolarAndEnergyData> getHistoryData(Integer plantId, LocalDateTime fromDateTime, LocalDateTime toDateTime);

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