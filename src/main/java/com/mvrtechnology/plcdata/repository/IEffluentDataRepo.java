package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.entity.EffluentData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IEffluentDataRepo extends JpaRepository<EffluentData,Long>
{
    Optional<EffluentData> findTopByPlantDetails_PlantIdOrderByDateAndTimeOfEffluentDesc(Integer plantId);
}
