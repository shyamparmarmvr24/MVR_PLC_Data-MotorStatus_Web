package com.mvrtechnology.plcdata.service;
import com.mvrtechnology.plcdata.entity.*;
import com.mvrtechnology.plcdata.util.ModbusReader;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SolarAndEnergyDataServiceImpl implements ISolarAndEnergyDataService
{

    @Override
    public SolarAndEnergyData fetchAndSaveSolarAndEnergy(PlantDetails plant, TCPMasterConnection connection)
    {
        try
        {
            SolarAndEnergyData data = new SolarAndEnergyData();

            data.setPlantDetails(plant);

            data.setDailyActProduction(round2(ModbusReader.readWord(connection,41200)*0.1));
            data.setTotalPower(round2(ModbusReader.readWord(connection,41261)*0.1));
            data.setInputPower(round2(ModbusReader.readWord(connection,41222)*0.1));

            data.setInverterDcCurrent(round2(ModbusReader.readWord(connection,41241)*0.1));
            data.setInverterDcVoltage(round2(ModbusReader.readWord(connection,41240)*0.1));

            data.setInverterCCurrent(round2(ModbusReader.readWord(connection,41218)*0.1));
            data.setInverterBCurrent(round2(ModbusReader.readWord(connection,41217)*0.1));
            data.setInverterACurrent(round2(ModbusReader.readWord(connection,41216)*0.1));

            data.setInverterABVoltage(round2(ModbusReader.readWord(connection,41210)*0.1));
            data.setInverterBCVoltage(round2(ModbusReader.readWord(connection,41211)*0.1));
            data.setInverterCAVoltage(round2(ModbusReader.readWord(connection,41212)*0.1));

            data.setRPhaseCurrent(round2(ModbusReader.readWord(connection,41100)*0.1));
            data.setYPhaseCurrent(round2(ModbusReader.readWord(connection,41101)*0.1));
            data.setBPhaseCurrent(round2(ModbusReader.readWord(connection,41102)*0.1));

            data.setFrequency(round2(ModbusReader.readWord(connection,41122)*0.1));
            data.setActiveEnergy(round2(ModbusReader.readWord(connection,41131)*0.1));

            data.setPowerFactor(round2(ModbusReader.readSignedInt16(connection,41121) * 0.001));

            data.setDateAndTimeOfSolarAndEnergy(LocalDateTime.now());
            data.setOperationDate(LocalDate.now());

            return data;
        }
        catch (Exception e)
        {
           return null;
        }
    }

    private BigDecimal round2(double val)
    {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
    }
}