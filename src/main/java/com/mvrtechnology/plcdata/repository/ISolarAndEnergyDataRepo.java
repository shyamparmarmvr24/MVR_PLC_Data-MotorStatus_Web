package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.entity.SolarAndEnergyData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolarAndEnergyDataRepo extends JpaRepository<SolarAndEnergyData,Long>
{

}
