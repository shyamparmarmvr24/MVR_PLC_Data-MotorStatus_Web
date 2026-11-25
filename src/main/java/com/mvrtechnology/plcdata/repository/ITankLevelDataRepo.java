package com.mvrtechnology.plcdata.repository;

import com.mvrtechnology.plcdata.entity.TankLevelData;
import com.mvrtechnology.plcdata.projection.TankLevelGraphProjection;
import com.mvrtechnology.plcdata.projection.TankLevelLatestProjection;
import com.mvrtechnology.plcdata.projection.TankLevelRawProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ITankLevelDataRepo extends JpaRepository<TankLevelData, Long>
{
    Optional<TankLevelData> findTopByPlantDetails_PlantIdOrderByDateAndTimeOfTankLevelDesc(
            Integer plantId
    );


    @Query(value = """
            SELECT
                Tank_Level_OperationID AS tankLevelOperationID,
                Date_And_Time_Of_Tank_Level AS dateAndTimeOfTankLevel,
                Tank_Level AS tankLevel,
                Sludge_Tank_Capacity AS sludgeTankCapacity
            FROM Tank_Level_Data
            WHERE Plant_Id = :plantId
              AND Date_And_Time_Of_Tank_Level >= :fromDateTime
              AND Date_And_Time_Of_Tank_Level <= :toDateTime
            ORDER BY Date_And_Time_Of_Tank_Level ASC
            """, nativeQuery = true)
    List<TankLevelRawProjection> findRawTankLevelData(
            @Param("plantId") Integer plantId,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime
    );


    // Get fixed tank capacity only once
    @Query(value = """
        SELECT TOP 1 Sludge_Tank_Capacity
        FROM Tank_Level_Data
        WHERE Plant_Id = :plantId
          AND Sludge_Tank_Capacity IS NOT NULL
        ORDER BY Date_And_Time_Of_Tank_Level DESC
        """, nativeQuery = true)
    Optional<Double> findTankCapacity(
            @Param("plantId") Integer plantId
    );


    @Query(value = """
        SELECT TOP 1
            Tank_Level_OperationID AS tankLevelOperationID,
            Date_And_Time_Of_Tank_Level AS dateAndTimeOfTankLevel,
            Tank_Level AS tankLevel,
            Sludge_Tank_Capacity AS sludgeTankCapacity
        FROM Tank_Level_Data
        WHERE Plant_Id = :plantId
          AND Date_And_Time_Of_Tank_Level >= :fromDateTime
          AND Date_And_Time_Of_Tank_Level < DATEADD(
                MINUTE,
                :intervalMinutes,
                :fromDateTime
          )
        ORDER BY Date_And_Time_Of_Tank_Level ASC
        """, nativeQuery = true)
    Optional<TankLevelRawProjection> findFirstRawReading(
            @Param("plantId") Integer plantId,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("intervalMinutes") Integer intervalMinutes
    );

    @Query(value = """
        SELECT
            b.bucketTime AS bucketTime,

            AVG(CAST(t.Tank_Level AS FLOAT))
                AS averageTankLevel,

            COUNT_BIG(*) AS recordCount

        FROM Tank_Level_Data t

        CROSS APPLY
        (
            SELECT
                DATEADD(
                    SECOND,
                    (
                        (
                            DATEDIFF(
                                SECOND,
                                :fromDateTime,
                                t.Date_And_Time_Of_Tank_Level
                            ) + (:intervalMinutes * 60) - 1
                        ) / (:intervalMinutes * 60)
                    ) * (:intervalMinutes * 60),
                    CAST(:fromDateTime AS DATETIME2)
                ) AS bucketTime
        ) b

        WHERE t.Plant_Id = :plantId

          AND t.Date_And_Time_Of_Tank_Level > :fromDateTime

          AND t.Date_And_Time_Of_Tank_Level <= :toDateTime

        GROUP BY b.bucketTime

        ORDER BY b.bucketTime ASC

        """, nativeQuery = true)
    List<TankLevelGraphProjection> findAverageTankLevelData(

            @Param("plantId") Integer plantId,

            @Param("fromDateTime") LocalDateTime fromDateTime,

            @Param("toDateTime") LocalDateTime toDateTime,

            @Param("intervalMinutes") Integer intervalMinutes
    );

    @Query(value = """
        WITH RankedData AS
        (
            SELECT
                Tank_Level_OperationID AS tankLevelOperationID,

                Date_And_Time_Of_Tank_Level AS dateAndTimeOfTankLevel,

                Tank_Level AS tankLevel,

                Sludge_Tank_Capacity AS sludgeTankCapacity,

                ROW_NUMBER() OVER (
                    PARTITION BY
                        DATEDIFF(
                            MINUTE,
                            :fromDateTime,
                            Date_And_Time_Of_Tank_Level
                        ) / :intervalMinutes

                    ORDER BY Date_And_Time_Of_Tank_Level ASC
                ) AS rn

            FROM Tank_Level_Data

            WHERE Plant_Id = :plantId

              AND Date_And_Time_Of_Tank_Level >= :fromDateTime

              AND Date_And_Time_Of_Tank_Level <= :toDateTime
        )

        SELECT
            tankLevelOperationID,
            dateAndTimeOfTankLevel,
            tankLevel,
            sludgeTankCapacity

        FROM RankedData

        WHERE rn = 1

        ORDER BY dateAndTimeOfTankLevel ASC

        """, nativeQuery = true)
    List<TankLevelRawProjection> findRawIntervalData(

            @Param("plantId") Integer plantId,

            @Param("fromDateTime") LocalDateTime fromDateTime,

            @Param("toDateTime") LocalDateTime toDateTime,

            @Param("intervalMinutes") Integer intervalMinutes
    );

    @Query(value = """
    SELECT TOP (1)
        Tank_Level AS tankLevel,
        Sludge_Tank_Capacity AS sludgeTankCapacity,
        Date_And_Time_Of_Tank_Level AS dateAndTimeOfTankLevel
    FROM Tank_Level_Data
    WHERE Plant_Id = :plantId
    ORDER BY
        Date_And_Time_Of_Tank_Level DESC,
        Tank_Level_OperationID DESC
    """, nativeQuery = true)
    Optional<TankLevelLatestProjection> findLatestTankLevel(
            @Param("plantId") Integer plantId
    );
}