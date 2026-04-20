package com.mvrtechnology.plcdata.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SolarAndEnergyData")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolarAndEnergyData
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solar_and_energy_seq")
    @SequenceGenerator(
            name = "solar_and_energy_seq",
            sequenceName = "SolarAndEnergySeq",
            allocationSize = 1,
            initialValue = 1
    )
    private Long operationID;

    @ManyToOne
    @JoinColumn(name = "Plant_Id", referencedColumnName = "Plant_Id")
    @JsonBackReference
    private PlantDetails plantDetails;

    private BigDecimal dailyActProduction;

    private BigDecimal totalPower;

    private BigDecimal inputPower;

    private BigDecimal inverterDcCurrent;

    private BigDecimal inverterDcVoltage;

    private BigDecimal inverterCCurrent;

    private BigDecimal inverterBCurrent;

    private BigDecimal inverterACurrent;

    private BigDecimal inverterABVoltage;

    private BigDecimal inverterBCVoltage;

    private BigDecimal inverterCAVoltage;

    private BigDecimal rPhaseCurrent;

    private BigDecimal yPhaseCurrent;

    private BigDecimal bPhaseCurrent;

    private BigDecimal frequency;

    private BigDecimal powerFactor;

    private BigDecimal activeEnergy;

    private LocalDateTime dateAndTimeOfSolarAndEnergy;

    private LocalDate operationDate;

}
