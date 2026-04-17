package com.mvrtechnology.plcdata.repository;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.entity.SolarAndEnergyData;
import com.mvrtechnology.plcdata.service.ISolarAndEnergyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SolarAndEnergyDataRepoMgmtImpl implements ISolarAndEnergyDataRepoImpl
{
    @Autowired
    private ISolarAndEnergyDataRepo solarAndEnergyRepo;

    @Autowired
    private ISolarAndEnergyDataService solarAndEnergyService;

    @Override
    public SolarAndEnergyData fetchAndSaveSolarAndEnergy(PlantDetails plant, TCPMasterConnection connection)
    {
        SolarAndEnergyData solarAndEnergyData = solarAndEnergyService.fetchAndSaveSolarAndEnergy(plant, connection);

        if (solarAndEnergyData != null)
        {
            return solarAndEnergyRepo.save(solarAndEnergyData);
        }
        return null;
    }
}
