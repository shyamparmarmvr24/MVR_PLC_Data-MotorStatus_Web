package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.entity.EffluentData;
import com.mvrtechnology.plcdata.projection.AllPlantEffluentProjection;
import com.mvrtechnology.plcdata.projection.EffluentAverageProjection;
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
            e.Cumulative_Flow -
            (
                SELECT TOP 1 ed.Cumulative_Flow
                FROM Effluent_Data ed
                WHERE ed.Plant_Id = e.Plant_Id
                  AND ed.Operation_Date = e.Operation_Date
                  AND ed.Cumulative_Flow IS NOT NULL
                  AND ed.Cumulative_Flow <> 0
                ORDER BY ed.Date_And_Time_Of_Effluent
            )
        ) AS CumulativeFlow
    
    FROM Effluent_Data e
    
    WHERE e.Plant_Id = :plantId
      AND e.Date_And_Time_Of_Effluent BETWEEN :fromDateTime AND :toDateTime
      AND e.Cumulative_Flow IS NOT NULL
      AND e.Cumulative_Flow <> 0
    
    ORDER BY e.Date_And_Time_Of_Effluent
    """, nativeQuery = true)
    List<Object[]> getDayWiseCumulativeFlow(
            Integer plantId,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime);

    @Query(value = """
    SELECT
    
    ISNULL(AVG(e.PH),0)            AS avgPH,
    ISNULL(AVG(e.COD),0)           AS avgCOD,
    ISNULL(AVG(e.BOD),0)           AS avgBOD,
    ISNULL(AVG(e.TSS),0)           AS avgTSS,
    ISNULL(AVG(e.Temperature),0)   AS avgTemperature,
    ISNULL(AVG(e.TN),0)            AS avgTN,
    ISNULL(AVG(e.Flow),0)          AS avgFlow,
    ISNULL(AVG(e.Velocity),0)      AS avgVelocity,
    
    ISNULL(
           (
               ISNULL
               (
                  (
                     SELECT TOP (1) ed.Cumulative_Flow
                     FROM Effluent_Data ed
                     WHERE ed.Plant_Id = :plantId
                     AND ed.Operation_Date = :operationDate
                     AND ed.Cumulative_Flow IS NOT NULL
                     AND ed.Cumulative_Flow <> 0
                     ORDER BY ed.Date_And_Time_Of_Effluent DESC
                  ),0
           )
    
           -
    
               ISNULL
               (
                   (
                      SELECT TOP (1) ed.Cumulative_Flow
                      FROM Effluent_Data ed
                      WHERE ed.Plant_Id = :plantId
                      AND ed.Operation_Date = :operationDate
                      AND ed.Cumulative_Flow IS NOT NULL
                      AND ed.Cumulative_Flow <> 0
                      ORDER BY ed.Date_And_Time_Of_Effluent ASC
                   ),0
               )
   
           ),0) AS cumulativeFlow
    
    FROM Effluent_Data e
    
    WHERE e.Plant_Id=:plantId
    AND e.Operation_Date=:operationDate
    
    """,nativeQuery = true)
    EffluentAverageProjection getAverageByPlantAndDate(Integer plantId, LocalDate operationDate);


    @Query(value = """
    SELECT

        e.Plant_Id AS plantId,

        p.Plant_Name AS plantName,

        ISNULL(AVG(e.PH), 0)            AS avgPH,
        ISNULL(AVG(e.COD), 0)           AS avgCOD,
        ISNULL(AVG(e.BOD), 0)           AS avgBOD,
        ISNULL(AVG(e.TSS), 0)           AS avgTSS,
        ISNULL(AVG(e.Temperature), 0)   AS avgTemperature,
        ISNULL(AVG(e.TN), 0)            AS avgTN,

        ISNULL(firstFlow.cumulativeFlow, 0)
            AS flowMeterReadingAm,

        ISNULL(lastFlow.cumulativeFlow, 0)
            AS flowMeterReadingPm,

        ISNULL(lastFlow.cumulativeFlow, 0)
        -
        ISNULL(firstFlow.cumulativeFlow, 0)
            AS cumulativeFlow

    FROM Effluent_Data e

    INNER JOIN Plant_Details p
        ON p.Plant_Id = e.Plant_Id

    OUTER APPLY
    (
        SELECT TOP (1)
            ed.Cumulative_Flow AS cumulativeFlow

        FROM Effluent_Data ed

        WHERE ed.Plant_Id = e.Plant_Id
          AND ed.Operation_Date = :operationDate
          AND ed.Cumulative_Flow IS NOT NULL
          AND ed.Cumulative_Flow <> 0

        ORDER BY ed.Date_And_Time_Of_Effluent ASC

    ) firstFlow

    OUTER APPLY
    (
        SELECT TOP (1)
            ed.Cumulative_Flow AS cumulativeFlow

        FROM Effluent_Data ed

        WHERE ed.Plant_Id = e.Plant_Id
          AND ed.Operation_Date = :operationDate
          AND ed.Cumulative_Flow IS NOT NULL
          AND ed.Cumulative_Flow <> 0

        ORDER BY ed.Date_And_Time_Of_Effluent DESC

    ) lastFlow

    WHERE e.Operation_Date = :operationDate

    GROUP BY
        e.Plant_Id,
        p.Plant_Name,
        firstFlow.cumulativeFlow,
        lastFlow.cumulativeFlow

    ORDER BY
        e.Plant_Id

    """, nativeQuery = true)
    List<AllPlantEffluentProjection> getAllPlantsAverageByDate(LocalDate operationDate);


//    @Query(value = """
//    SELECT
//
//        e.Plant_Id AS plantId,
//
//        p.Plant_Name AS plantName,
//
//        ISNULL(AVG(e.PH), 0)            AS avgPH,
//        ISNULL(AVG(e.COD), 0)           AS avgCOD,
//        ISNULL(AVG(e.BOD), 0)           AS avgBOD,
//        ISNULL(AVG(e.TSS), 0)           AS avgTSS,
//        ISNULL(AVG(e.Temperature), 0)   AS avgTemperature,
//        ISNULL(AVG(e.TN), 0)            AS avgTN,
//
//        ISNULL(
//                            (
//                                SELECT TOP (1)
//                                    ed.Cumulative_Flow
//                                FROM Effluent_Data ed
//                                WHERE ed.Plant_Id = e.Plant_Id
//                                  AND ed.Operation_Date = :operationDate
//                                  AND ed.Cumulative_Flow IS NOT NULL
//                                  AND ed.Cumulative_Flow <> 0
//                                ORDER BY ed.Date_And_Time_Of_Effluent ASC
//                            ),
//                            0
//                        ) AS flowMeterReadingAm,
//
//                        ISNULL(
//                            (
//                                SELECT TOP (1)
//                                    ed.Cumulative_Flow
//                                FROM Effluent_Data ed
//                                WHERE ed.Plant_Id = e.Plant_Id
//                                  AND ed.Operation_Date = :operationDate
//                                  AND ed.Cumulative_Flow IS NOT NULL
//                                  AND ed.Cumulative_Flow <> 0
//                                ORDER BY ed.Date_And_Time_Of_Effluent DESC
//                            ),
//                            0
//                        ) AS flowMeterReadingPm,
//
//        ISNULL(
//            (
//                ISNULL
//                (
//                    (
//                        SELECT TOP (1)
//                            ed.Cumulative_Flow
//                        FROM Effluent_Data ed
//                        WHERE ed.Plant_Id = e.Plant_Id
//                          AND ed.Operation_Date = :operationDate
//                          AND ed.Cumulative_Flow IS NOT NULL
//                          AND ed.Cumulative_Flow <> 0
//                        ORDER BY ed.Date_And_Time_Of_Effluent DESC
//                    ),
//                    0
//                )
//            )
//            -
//            ISNULL
//            (
//                (
//                    SELECT TOP (1)
//                        ed.Cumulative_Flow
//                    FROM Effluent_Data ed
//                    WHERE ed.Plant_Id = e.Plant_Id
//                      AND ed.Operation_Date = :operationDate
//                      AND ed.Cumulative_Flow IS NOT NULL
//                      AND ed.Cumulative_Flow <> 0
//                    ORDER BY ed.Date_And_Time_Of_Effluent ASC
//                ),
//                0
//            ),
//            0
//        ) AS cumulativeFlow
//
//    FROM Effluent_Data e
//
//    INNER JOIN Plant_Details p
//        ON p.Plant_Id = e.Plant_Id
//
//    WHERE e.Operation_Date = :operationDate
//
//    GROUP BY
//        e.Plant_Id,
//        p.Plant_Name
//
//    ORDER BY
//        e.Plant_Id
//
//    """, nativeQuery = true)
//    List<AllPlantEffluentProjection> getAllPlantsAverageByDate(LocalDate operationDate);
}