package com.mvrtechnology.plcdata.service;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.entity.SolarAndEnergyData;

public interface ISolarAndEnergyDataService
{
    public SolarAndEnergyData fetchAndSaveSolarAndEnergy(PlantDetails plant, TCPMasterConnection connection);
}
