package com.mvrtechnology.plcdata.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "Tank_Level_Data",
        indexes = {
                @Index(
                        name = "IDX_TANK_LEVEL_PLANT_DATETIME",
                        columnList = "Plant_Id,Date_And_Time_Of_Tank_Level"
                ),
                @Index(
                        name = "IDX_TANK_LEVEL_PLANT_OPERATION_DATE",
                        columnList = "Plant_Id,Operation_Date"
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TankLevelData
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tank_level_data_seq"
    )
    @SequenceGenerator(
            name = "tank_level_data_seq",
            sequenceName = "TankLevelDataSeq",
            allocationSize = 1,
            initialValue = 1
    )
    private Long tankLevelOperationID;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "Plant_Id",
            referencedColumnName = "Plant_Id",
            nullable = false
    )
    @JsonBackReference
    private PlantDetails plantDetails;


    @Column(name = "Tank_Level")
    private Double tankLevel;

    @Column(name = "SludgeTankCapacity")
    private Double sludgeTankCapacity;


    @Column(name = "Date_And_Time_Of_Tank_Level", nullable = false)
    private LocalDateTime dateAndTimeOfTankLevel;


    @Column(name = "Operation_Date", nullable = false)
    private LocalDate operationDate;
}