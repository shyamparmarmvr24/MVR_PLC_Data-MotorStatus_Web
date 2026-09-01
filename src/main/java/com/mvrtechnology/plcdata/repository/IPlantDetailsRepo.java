package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.dtos.PlantDetailsResponseDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IPlantDetailsRepo extends JpaRepository<PlantDetails,Integer>
{
    @Query("""
        SELECT new com.mvrtechnology.plcdata.dtos.PlantDetailsResponseDTO(
            p.plantId,
            p.plantName,
            p.zone,
            p.plantKLD,
            p.district,
            p.isMNITCompleted,
            p.mnitCompletionDate,
            p.isSolarCompleted,
            p.solarCompletionDate,
            p.latitude,
            p.longitude
        )
        FROM PlantDetails p
        WHERE p.plantId = :plantId
        """)
    Optional<PlantDetailsResponseDTO> findPlantDetailsByPlantId(@Param("plantId") Integer plantId);

}
