package com.mvrtechnology.plcdata.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "Scada_Access_Plant",
        uniqueConstraints = {

                @UniqueConstraint(
                        name = "UK_SCADA_ACCESS_PLANT",
                        columnNames = {
                                "Access_Id",
                                "Plant_Id"
                        }
                )
        },
        indexes = {

                @Index(
                        name = "IDX_SCADA_ACCESS_PLANT_ACCESS",
                        columnList = "Access_Id"
                ),

                @Index(
                        name = "IDX_SCADA_ACCESS_PLANT_PLANT",
                        columnList = "Plant_Id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScadaAccessPlant
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "access_plant_sequence"
    )
    @SequenceGenerator(
            name = "access_plant_sequence",
            sequenceName = "AccessPlantSequence",
            allocationSize = 1,
            initialValue = 1
    )
    private Long id;


    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "Access_Id",
            nullable = false
    )
    private ScadaAccessControl accessControl;


    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "Plant_Id",
            referencedColumnName = "Plant_Id",
            nullable = false
    )
    private PlantDetails plant;
}