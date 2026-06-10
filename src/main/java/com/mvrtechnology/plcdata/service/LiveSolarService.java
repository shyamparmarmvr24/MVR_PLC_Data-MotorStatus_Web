package com.mvrtechnology.plcdata.service;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.dtos.LiveSolarDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.util.ModbusReader;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class LiveSolarService
{
    public LiveSolarDTO readLiveData(PlantDetails plant, TCPMasterConnection connection) throws Exception
    {
        LiveSolarDTO dto = new LiveSolarDTO();

        dto.setPlantId(plant.getPlantId());

        dto.setDailyActProduction(
                round2(ModbusReader.readWord(connection,41200)*0.1));

        dto.setTotalPower(
                round2(ModbusReader.readWord(connection,41261)*0.1));

        dto.setInputPower(
                round2(ModbusReader.readWord(connection,41222)*0.1));

        dto.setInverterDcCurrent(
                round2(ModbusReader.readWord(connection,41241)*0.1));

        dto.setInverterDcVoltage(
                round2(ModbusReader.readWord(connection,41240)*0.1));

        dto.setInverterCCurrent(
                round2(ModbusReader.readWord(connection,41218)*0.1));

        dto.setInverterBCurrent(
                round2(ModbusReader.readWord(connection,41217)*0.1));

        dto.setInverterACurrent(
                round2(ModbusReader.readWord(connection,41216)*0.1));

        dto.setInverterABVoltage(
                round2(ModbusReader.readWord(connection,41210)*0.1));

        dto.setInverterBCVoltage(
                round2(ModbusReader.readWord(connection,41211)*0.1));

        dto.setInverterCAVoltage(
                round2(ModbusReader.readWord(connection,41212)*0.1));

        dto.setRPhaseCurrent(
                round2(ModbusReader.readWord(connection,41100)*0.1));

        dto.setYPhaseCurrent(
                round2(ModbusReader.readWord(connection,41101)*0.1));

        dto.setBPhaseCurrent(
                round2(ModbusReader.readWord(connection,41102)*0.1));

        dto.setFrequency(
                round2(ModbusReader.readWord(connection,41122)*0.1));

        dto.setActiveEnergy(
                round2(ModbusReader.readWord(connection,41131)*0.1));

        dto.setPowerFactor(
                round2(ModbusReader.readSignedInt16(connection,41121) * 0.001));

        dto.setTimeStamp(LocalDateTime.now());

        return dto;
    }

    private BigDecimal round2(double val)
    {
        return BigDecimal.valueOf(val)
                .setScale(2, RoundingMode.HALF_UP);
    }
}