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

//    @Query(value = """
//    SELECT
//        Operation_Date as operationDate,
//
//        AVG(PH) as avgPH,
//        AVG(COD) as avgCod,
//        AVG(BOD) as avgBod,
//        AVG(TSS) as avgTss,
//        AVG(Temperature) as avgTemperature,
//        AVG(TN) as avgTN,
//        AVG(Flow) as avgFlow,
//        AVG(Velocity) as avgVelocity,
//        AVG(Cumulative_Flow) as avgCumulativeFlow
//
//    FROM Effluent_Data
//
//    WHERE Plant_Id = :plantId
//    AND Operation_Date BETWEEN :fromDate AND :toDate
//
//    GROUP BY Operation_Date
//
//    ORDER BY Operation_Date
//    """, nativeQuery = true)
//    List<Object[]> getDateRangeAverage(Integer plantId, LocalDate fromDate, LocalDate toDate);

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
        ORDER BY ed.date_And_Time_Of_Effluent DESC
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
}
