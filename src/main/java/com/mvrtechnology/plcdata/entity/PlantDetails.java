package com.mvrtechnology.plcdata.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Plant_Details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantDetails
{
    @Id
    @Column(name = "Plant_Id", nullable = false, unique = true)
    private Integer plantId;

    @Column(name = "Plant_Name", nullable = false, length = 50)
    private String plantName;

    @Column(name = "Zone" , nullable = false)
    private Integer zone;

    @Column(name = "PLC_IP", nullable = false)
    private String plcIp;

    @Column(name="PLC_Port", nullable = false)
    private Integer plcPort;

    @Column(name = "PLC_Status")
    private Boolean plcStatus;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "plantDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SolarAndEnergyData> solarAndPowerOperations = new ArrayList<>();

    @OneToMany(mappedBy = "plantDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<EffluentData> effluentOperations = new ArrayList<>();
}
