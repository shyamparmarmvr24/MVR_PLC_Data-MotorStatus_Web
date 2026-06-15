package com.mvrtechnology.plcdata.service;
import com.mvrtechnology.plcdata.dtos.MotorStatusDTO;
import com.mvrtechnology.plcdata.util.ModbusReader;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.springframework.stereotype.Service;

@Service
public class MotorStatusService
{

    public MotorStatusDTO fetchMotorStatus(TCPMasterConnection connection)
    {

        MotorStatusDTO dto = new MotorStatusDTO();

        try {

            int b1Base = 8392;
            boolean[] b1 = ModbusReader.readMultipleCoils(connection, b1Base, 30);

            int b2Base = 8492;
            boolean[] b2 = ModbusReader.readMultipleCoils(connection, b2Base, 20);

            int b3Base = 8504;
            boolean[] b3 = ModbusReader.readMultipleCoils(connection, b3Base, 25);

            dto.setSrs1OnOf(b1[8392 - b1Base]);
            dto.setSrs2OnOf(b1[8393 - b1Base]);
            dto.setPolymerMixerOnOf(b1[8394 - b1Base]);
            dto.setDewateringMixerOnOf(b1[8395 - b1Base]);
            dto.setDewateringScrew1_2OnOf(b1[8396 - b1Base]);
            dto.setScrewConveyor1OnOf(b1[8397 - b1Base]);
            dto.setDryerOnOf(b1[8398 - b1Base]);
            dto.setDucting1_2OnOf(b1[8399 - b1Base]);
            dto.setScrewConveyor2OnOf(b1[8400 - b1Base]);
            dto.setPyro1OnOf(b1[8401 - b1Base]);
            dto.setPyro2OnOf(b1[8402 - b1Base]);

            dto.setSludgeSubPumpOnOf(b1[8404 - b1Base]);
            dto.setFilterFeedPumpOnOf(b1[8405 - b1Base]);
            dto.setCheDosPumpOnOf(b1[8406 - b1Base]);
            dto.setPyroExhaustFanOnOf(b1[8407 - b1Base]);
            dto.setTreatedWaterPumpOnOf(b1[8408 - b1Base]);
            dto.setNonSubPumpOnOf(b1[8409 - b1Base]);
            dto.setPyroBlowerOnOf(b1[8410 - b1Base]);
            dto.setPolyDosingOnOf(b1[8411 - b1Base]);
            dto.setBlowerOnOf(b1[8412 - b1Base]);
            dto.setMcCleaningOnOf(b1[8413 - b1Base]);
            dto.setWaterBoosterPumpOnOf(b1[8415 - b1Base]);
            dto.setUnderWaterPumpOnOf(b1[8416 - b1Base]);
            dto.setWaterSubPumpOnOf(b1[8417 - b1Base]);

            dto.setSrs1MotorTrip(b2[8492 - b2Base]);
            dto.setSrs2MotorTrip(b2[8493 - b2Base]);
            dto.setPolymerMixerTrip(b2[8494 - b2Base]);
            dto.setDewateringMixerTrip(b2[8495 - b2Base]);
            dto.setDewateringScrewPress1Trip(b2[8496 - b2Base]);
            dto.setScrewConveyor1Trip(b2[8497 - b2Base]);
            dto.setDryerMotorTrip(b2[8498 - b2Base]);
            dto.setDuctingMotor1Trip(b2[8499 - b2Base]);
            dto.setScrewConveyor2Trip(b2[8500 - b2Base]);
            dto.setPyrolizerMotor1Trip(b2[8501 - b2Base]);
            dto.setPyrolizerMotor2Trip(b2[8502 - b2Base]);
            dto.setDuctingMotor2Trip(b2[8503 - b2Base]);

            dto.setSubmersiblePumpTrip(b3[8504 - b3Base]);
            dto.setFilterFeedPumpTrip(b3[8506 - b3Base]);
            dto.setChemicalDosingPumpTrip(b3[8508 - b3Base]);
            dto.setPyrolyzerExhaustFanTrip(b3[8510 - b3Base]);
            dto.setTreatedWaterPumpTrip(b3[8512 - b3Base]);
            dto.setNonSubmersiblePumpTrip(b3[8514 - b3Base]);
            dto.setPyrolyzerBlowerFanTrip(b3[8516 - b3Base]);
            dto.setPolymerDosingMotorTrip(b3[8518 - b3Base]);
            dto.setBlowerMotorTrip(b3[8520 - b3Base]);
            dto.setDeWateringScrewPress2Trip(b3[8522 - b3Base]);
            dto.setPressurePumpTrip(b3[8523 - b3Base]);
            dto.setEmergencyBottonPressed(b3[8524 - b3Base]);

            dto.setEmg(ModbusReader.readCoil(connection, 1001));
            dto.setSm400(ModbusReader.readCoil(connection, 20881));

            //temperature
//            dto.setHotWaterOutlet(ModbusReader.readWord(connection,40504)*0.1);
//            dto.setHotWaterInlet(ModbusReader.readWord(connection,40503)*0.1);
//            dto.setExhaustChimney(ModbusReader.readWord(connection,40502)*0.1);
//            dto.setPyrolyzerMiddle(ModbusReader.readWord(connection,40501)*0.1);
//            dto.setPyrolyzerBottom(ModbusReader.readWord(connection,40500)*0.1);
//            dto.setScreenChamberLevel(ModbusReader.readWord(connection,40602)*1.0);

            int[] temp = ModbusReader.readWords(connection, 40500, 5);

            dto.setPyrolyzerBottom(temp[0] * 0.1);
            dto.setPyrolyzerMiddle(temp[1] * 0.1);
            dto.setExhaustChimney(temp[2] * 0.1);
            dto.setHotWaterInlet(temp[3] * 0.1);
            dto.setHotWaterOutlet(temp[4] * 0.1);

            dto.setScreenChamberLevel(ModbusReader.readWord(connection, 40602)*1.0);

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return dto;
    }
}