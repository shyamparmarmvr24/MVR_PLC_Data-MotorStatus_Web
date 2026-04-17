package com.mvrtechnology.plcdata.repository;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.entity.EffluentData;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.service.IEffluentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class EffluentDataRepoImplMgmt implements IEffluentDataRepoImpl
{
    @Autowired
    private IEffluentDataService effluentService;

    @Autowired
    private IEffluentDataRepo effluentRepo;

    @Override
    public EffluentData fetchAndSaveEffluentData(PlantDetails plant, TCPMasterConnection connection)
    {
        EffluentData effluentData = effluentService.fetchAndSaveEffluentData(plant, connection);

        if (effluentData != null && !isAllZero(effluentData))
        {
            return effluentRepo.save(effluentData);
        }

        System.out.println("Skipped zero data for plant: " + plant.getPlantName());
        return null;
    }

    private boolean isZero(BigDecimal val)
    {
        return val == null || val.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isAllZero(EffluentData d)
    {
        return isZero(d.getPH()) &&
                isZero(d.getCod()) &&
                isZero(d.getBod()) &&
                isZero(d.getTss()) &&
                isZero(d.getTemperature()) &&
                isZero(d.getTN()) &&
                isZero(d.getFlow()) &&
                isZero(d.getVelocity()) &&
                isZero(d.getCumulativeFlow());
    }
}
