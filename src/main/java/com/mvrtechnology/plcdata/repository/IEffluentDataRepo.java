package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.entity.EffluentData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEffluentDataRepo extends JpaRepository<EffluentData,Long>
{

}
