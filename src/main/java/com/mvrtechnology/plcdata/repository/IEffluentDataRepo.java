package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.entity.EffluentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IEffluentDataRepo extends JpaRepository<EffluentData,Long>
{
    @Query(value = """
    SELECT TOP 1 *
    FROM Effluent_Data
    WHERE Plant_Id = :plantId
    ORDER BY Date_And_Time_Of_Effluent DESC
    """, nativeQuery = true)
    Optional<EffluentData> getLatest(Integer plantId);

    @Query("""
    SELECT e
    FROM EffluentData e
    WHERE e.plantDetails.plantId = :plantId
    AND e.dateAndTimeOfEffluent BETWEEN :fromDateTime AND :toDateTime
    ORDER BY e.dateAndTimeOfEffluent
    """)
    List<EffluentData> getDataBetweenTime(Integer plantId, LocalDateTime fromDateTime, LocalDateTime toDateTime);

    @Query(value = """
    SELECT
        e.Operation_Date,

        AVG(e.PH) AS avgPH,
        AVG(e.COD) AS avgCod,
        AVG(e.BOD) AS avgBod,
        AVG(e.TSS) AS avgTss,
        AVG(e.Temperature) AS avgTemperature,
        AVG(e.TN) AS avgTN,
        AVG(e.Flow) AS avgFlow,
        AVG(e.Velocity) AS avgVelocity,

        firstRecord.Cumulative_Flow AS firstFlow,
        lastRecord.Cumulative_Flow AS lastFlow

    FROM Effluent_Data e

    OUTER APPLY
    (
        SELECT TOP (1)
            ed.Cumulative_Flow
        FROM Effluent_Data ed
        WHERE ed.Plant_Id = e.Plant_Id
          AND ed.Operation_Date = e.Operation_Date
        ORDER BY ed.date_And_Time_Of_Effluent ASC
    ) firstRecord

    OUTER APPLY
    (
        SELECT TOP (1)
        ed.Cumulative_Flow
        FROM Effluent_Data ed
        WHERE ed.Plant_Id = e.Plant_Id
        AND ed.Operation_Date = e.Operation_Date
        AND ISNULL(ed.Cumulative_Flow, 0) <> 0
        ORDER BY ed.Date_And_Time_Of_Effluent DESC
    ) lastRecord
    
    WHERE e.Plant_Id = :plantId
      AND e.Operation_Date BETWEEN :fromDate AND :toDate

    GROUP BY
        e.Operation_Date,
        firstRecord.Cumulative_Flow,
        lastRecord.Cumulative_Flow

    ORDER BY e.Operation_Date
    """, nativeQuery = true)
    List<Object[]> getDateRangeAverage(Integer plantId, LocalDate fromDate, LocalDate toDate);


    @Query("""
    SELECT DISTINCT e.operationDate
    FROM EffluentData e
    WHERE e.plantDetails.plantId = :plantId
    AND YEAR(e.operationDate) = :year
    AND MONTH(e.operationDate) = :month
    ORDER BY e.operationDate
    """)
    List<LocalDate> getAvailableDates(Integer plantId, int year, int month);

    @Query(value = """
    SELECT
    
    e.Date_And_Time_Of_Effluent,
    
    (
    ISNULL(e.Cumulative_Flow,0)
    
    -
    
    ISNULL
    (
    (
    SELECT TOP 1
    ed.Cumulative_Flow
    
    FROM Effluent_Data ed
    
    WHERE ed.Plant_Id=e.Plant_Id
    AND ed.Operation_Date=e.Operation_Date
    
    ORDER BY ed.Date_And_Time_Of_Effluent
    )
    ,0)
    
    ) AS CumulativeFlow
    
    FROM Effluent_Data e
    
    WHERE e.Plant_Id=:plantId
    
    AND e.Date_And_Time_Of_Effluent
    BETWEEN :fromDateTime AND :toDateTime
    
    ORDER BY e.Date_And_Time_Of_Effluent
    
    """,nativeQuery=true)
    List<Object[]> getDayWiseCumulativeFlow(Integer plantId, LocalDateTime fromDateTime, LocalDateTime toDateTime);
}
