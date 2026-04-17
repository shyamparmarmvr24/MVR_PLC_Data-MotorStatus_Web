package com.mvrtechnology.plcdata.service;
import com.mvrtechnology.plcdata.dtos.MotorStatusDTO;
import com.mvrtechnology.plcdata.util.CoilAddresses;
import com.mvrtechnology.plcdata.util.ModbusReader;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.springframework.stereotype.Service;

@Service
public class MotorStatusService {

    public MotorStatusDTO fetchMotorStatus(TCPMasterConnection connection) {

        MotorStatusDTO dto = new MotorStatusDTO();

        try {
            dto.setEmg(ModbusReader.readCoil(connection, CoilAddresses.EMG+1));
            dto.setSm400(ModbusReader.readCoil(connection, CoilAddresses.SM400+1));

            dto.setTreatedWaterPumpOnOf(ModbusReader.readCoil(connection, CoilAddresses.TREATED_WATER_PUMP_ON_OF+1));
            dto.setSludgeSubPumpOnOf(ModbusReader.readCoil(connection, CoilAddresses.SLUDGE_SUB_PUMP_ON_OF+1));
            dto.setPyroExhaustFanOnOf(ModbusReader.readCoil(connection, CoilAddresses.PYRO_EXHAUST_FAN_ON_OF+1));
            dto.setPyroBlowerOnOf(ModbusReader.readCoil(connection, CoilAddresses.PYRO_BLOWER_ON_OF+1));

            dto.setPolyDosingOnOf(ModbusReader.readCoil(connection, CoilAddresses.POLY_DOSING_ON_OF+1));
            dto.setNonSubPumpOnOf(ModbusReader.readCoil(connection, CoilAddresses.NON_SUB_PUMP_ON_OF+1));
            dto.setMcCleaningOnOf(ModbusReader.readCoil(connection, CoilAddresses.M_C_CLEANING_ON_OF+1));
            dto.setFilterFeedPumpOnOf(ModbusReader.readCoil(connection, CoilAddresses.FILTER_FEED_PUMP_ON_OF+1));

            dto.setCheDosPumpOnOf(ModbusReader.readCoil(connection, CoilAddresses.CHE_DOS_PUMP_ON_OF+1));
            dto.setBlowerOnOf(ModbusReader.readCoil(connection, CoilAddresses.BLOWER_ON_OF+1));
            dto.setWaterSubPumpOnOf(ModbusReader.readCoil(connection, CoilAddresses.WATER_SUB_PUMP_ON_OF+1));
            dto.setUnderWaterPumpOnOf(ModbusReader.readCoil(connection, CoilAddresses.UNDER_WATER_PUMP_ON_OF+1));

            dto.setWaterBoosterPumpOnOf(ModbusReader.readCoil(connection, CoilAddresses.WATER_BOSTER_PUMP_ON_OF+1));
            dto.setSrs1OnOf(ModbusReader.readCoil(connection, CoilAddresses.SRS_1_ON_OFF+1));
            dto.setSrs2OnOf(ModbusReader.readCoil(connection, CoilAddresses.SRS_2_ON_OFF+1));
            dto.setPolymerMixerOnOf(ModbusReader.readCoil(connection, CoilAddresses.POLYMERMIXER_ON_OFF+1));

            dto.setDewateringMixerOnOf(ModbusReader.readCoil(connection, CoilAddresses.DEWATERINGMIXER_ON_OFF+1));
            dto.setDewateringScrew1_2OnOf(ModbusReader.readCoil(connection, CoilAddresses.DEWATERINGSCREW_1_2_ON_OFF+1));
            dto.setScrewConveyor1OnOf(ModbusReader.readCoil(connection, CoilAddresses.SCREWCONVEYOR_1_ON_OFF+1));
            dto.setDryerOnOf(ModbusReader.readCoil(connection, CoilAddresses.DRYER_ON_OFF+1));

            dto.setDucting1_2OnOf(ModbusReader.readCoil(connection, CoilAddresses.DUCTING_1_2_ON_OFF+1));
            dto.setScrewConveyor2OnOf(ModbusReader.readCoil(connection, CoilAddresses.SCREWCONVEYOR_2_ON_OFF+1));
            dto.setPyro1OnOf(ModbusReader.readCoil(connection, CoilAddresses.PYRO_1_ON_OFF+1));
            dto.setPyro2OnOf(ModbusReader.readCoil(connection, CoilAddresses.PYRO_2_ON_OFF+1));

            dto.setSubmersiblePumpTrip(ModbusReader.readCoil(connection, CoilAddresses.SUBMERSIBLE_PUMP_TRIP+1));
            dto.setFilterFeedPumpTrip(ModbusReader.readCoil(connection, CoilAddresses.FILTER_FEED_PUMPTRIP+1));
            dto.setChemicalDosingPumpTrip(ModbusReader.readCoil(connection, CoilAddresses.CHEMICAL_DOSING_PUMPTRIP+1));
            dto.setPyrolyzerExhaustFanTrip(ModbusReader.readCoil(connection, CoilAddresses.PYROLYZER_EXHAUST_FANTRIP+1));
            dto.setNonSubmersiblePumpTrip(ModbusReader.readCoil(connection, CoilAddresses.NON_SUBMERSIBLE_PUMPTRIP+1));
            dto.setPyrolyzerBlowerFanTrip(ModbusReader.readCoil(connection, CoilAddresses.PYROLYZER_BLOWER_FANTRIP+1));

            dto.setPolymerDosingMotorTrip(ModbusReader.readCoil(connection, CoilAddresses.POLYMER_DOSING_MOTORTRIP+1));
            dto.setBlowerMotorTrip(ModbusReader.readCoil(connection, CoilAddresses.BLOWER_MOTORTRIP+1));
            dto.setSrs1MotorTrip(ModbusReader.readCoil(connection, CoilAddresses.SRS_1_MOTOR_TRIP+1));
            dto.setSrs2MotorTrip(ModbusReader.readCoil(connection, CoilAddresses.SRS_2_MOTOR_TRIP+1));
            dto.setDewateringMixerTrip(ModbusReader.readCoil(connection, CoilAddresses.DE_WATERING_MIXER_TRIP+1));
            dto.setDewateringScrewPress1Trip(ModbusReader.readCoil(connection, CoilAddresses.DE_WATERING_SCREW_PRESS_1_TRIP+1));

            dto.setPolymerMixerTrip(ModbusReader.readCoil(connection, CoilAddresses.POLYMER_MIXER_TRIP+1));
            dto.setScrewConveyor1Trip(ModbusReader.readCoil(connection, CoilAddresses.SCREW_CONV_1_TRIP+1));
            dto.setScrewConveyor2Trip(ModbusReader.readCoil(connection, CoilAddresses.SCREW_CONV_2_TRIP+1));
            dto.setDryerMotorTrip(ModbusReader.readCoil(connection, CoilAddresses.DRYER_MOTOR_TRIP+1));
            dto.setPyrolizerMotor1Trip(ModbusReader.readCoil(connection, CoilAddresses.PYROLYZER_MOTOR_1_TRIP+1));
            dto.setPyrolizerMotor2Trip(ModbusReader.readCoil(connection, CoilAddresses.PYROLYZER_MOTOR_2_TRIP+1));

            dto.setDuctingMotor1Trip(ModbusReader.readCoil(connection, CoilAddresses.DUCTING_MOTOR_1_TRIP+1));
            dto.setDuctingMotor2Trip(ModbusReader.readCoil(connection, CoilAddresses.DUCTING_MOTOR_2_TRIP+1));
            dto.setTreatedWaterPumpTrip(ModbusReader.readCoil(connection, CoilAddresses.TREATED_WATER_PUMP_TRIP+1));
            dto.setPressurePumpTrip(ModbusReader.readCoil(connection, CoilAddresses.PRESSURE_PUMP_TRIP+1));
            dto.setEmergencyBottonPressed(ModbusReader.readCoil(connection, CoilAddresses.EMERGENCY_BUTTON_PRESSED+1));
            dto.setDeWateringScrewPress2Trip(ModbusReader.readCoil(connection, CoilAddresses.DE_WATERING_SCREW_PRESS__2_TRIP+1));

        }
        catch (Exception e)
        {
            throw new RuntimeException("Motor PLC Read Failed"+ e +" "+e.getMessage());
        }

        return dto;
    }
}