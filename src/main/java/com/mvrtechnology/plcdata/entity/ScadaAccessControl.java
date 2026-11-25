package com.mvrtechnology.plcdata.entity;

import com.mvrtechnology.plcdata.enums.ScadaAccessType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "Scada_Access_Control",
        indexes = {

                @Index(
                        name = "IDX_SCADA_ACCESS_IP",
                        columnList = "Ip_Address"
                ),

                @Index(
                        name = "IDX_SCADA_ACCESS_TYPE_ACTIVE",
                        columnList = "Access_Type, Is_Active"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScadaAccessControl
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "access_control_sequence"
    )
    @SequenceGenerator(
            name = "access_control_sequence",
            sequenceName = "AccessControlSequence",
            allocationSize = 1,
            initialValue = 1
    )
    private Long accessId;


    @Column(
            name = "Ip_Address",
            nullable = false,
            length = 45
    )
    private String ipAddress;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "Access_Type",
            nullable = false,
            length = 20
    )
    private ScadaAccessType accessType;

    @OneToMany(
            mappedBy = "accessControl",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<ScadaAccessPlant> plants =
            new HashSet<>();


    @Column(
            name = "Is_Active",
            nullable = false
    )
    private Boolean isActive;


    @Column(
            name = "Description",
            length = 255
    )
    private String description;


    @Column(
            name = "Created_At",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(name = "Updated_At")
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate()
    {
        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;

        if (isActive == null)
        {
            isActive = true;
        }
    }


    @PreUpdate
    protected void onUpdate()
    {
        updatedAt = LocalDateTime.now();
    }
}