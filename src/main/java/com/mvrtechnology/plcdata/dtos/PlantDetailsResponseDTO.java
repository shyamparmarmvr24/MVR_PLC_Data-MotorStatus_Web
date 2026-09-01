package com.mvrtechnology.plcdata.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantDetailsResponseDTO {

    private Integer plantId;
    private String plantName;
    private Integer zone;
    private Integer plantKLD;
    private String district;
    private Boolean isMNITCompleted;
    private LocalDate mnitCompletionDate;
    private Boolean isSolarCompleted;
    private LocalDate solarCompletionDate;
    private BigDecimal latitude;
    private BigDecimal longitude;
}