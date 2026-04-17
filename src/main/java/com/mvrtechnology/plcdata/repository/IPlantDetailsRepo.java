package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPlantDetailsRepo extends JpaRepository<PlantDetails,Integer>
{

}
