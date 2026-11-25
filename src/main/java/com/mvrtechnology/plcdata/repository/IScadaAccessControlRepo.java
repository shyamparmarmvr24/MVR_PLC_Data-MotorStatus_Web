package com.mvrtechnology.plcdata.repository;
import com.mvrtechnology.plcdata.entity.ScadaAccessControl;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IScadaAccessControlRepo
        extends JpaRepository<ScadaAccessControl, Long>
{
    List<ScadaAccessControl> findByIsActiveTrue();
}