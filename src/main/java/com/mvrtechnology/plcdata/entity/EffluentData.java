package com.mvrtechnology.plcdata.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Effluent_Data",
        indexes = {
                @Index(name = "IDX_EFFLUENT_LATEST",
                        columnList = "Plant_Id,Date_And_Time_Of_Effluent")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EffluentData
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "effluent_data_seq")
    @SequenceGenerator(
            name = "effluent_data_seq",
            sequenceName = "EffluentDataSeq",
            allocationSize = 1,
            initialValue = 1
    )
    private Long operationID;

    @ManyToOne
    @JoinColumn(name = "Plant_Id", referencedColumnName = "Plant_Id")
    @JsonBackReference
    private PlantDetails plantDetails;

    private BigDecimal pH;

    private BigDecimal cod;

    private BigDecimal bod;

    private BigDecimal tss;

    private BigDecimal temperature;

    private BigDecimal tN;

    private BigDecimal flow;

    private BigDecimal velocity;

    private BigDecimal cumulativeFlow;

    private Boolean filterFeedPumpStatus;

    private LocalDateTime dateAndTimeOfEffluent;

    private LocalDate operationDate;
}