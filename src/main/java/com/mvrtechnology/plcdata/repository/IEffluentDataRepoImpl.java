package com.mvrtechnology.plcdata.repository;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.entity.EffluentData;
import com.mvrtechnology.plcdata.entity.PlantDetails;

public interface IEffluentDataRepoImpl
{
    public EffluentData fetchAndSaveEffluentData(PlantDetails plant, TCPMasterConnection connection);
}
