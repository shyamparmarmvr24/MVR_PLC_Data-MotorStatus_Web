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
public class EffluentDataServiceImpl implements IEffluentDataService
{

    @Override
    public EffluentData fetchAndSaveEffluentData(PlantDetails plant, TCPMasterConnection connection)
    {
        try
        {
            EffluentData data = new EffluentData();

            data.setPlantDetails(plant);

            data.setPH(round2(ModbusReader.readFloat(connection,42004)));
            data.setCod(round2(ModbusReader.readFloat(connection,40402)));
            data.setBod(round2(ModbusReader.readFloat(connection,40406)));
            data.setTss(round2(ModbusReader.readFloat(connection,40408)));

            data.setTemperature(round2(ModbusReader.readWord(connection,41000)*0.1));

            data.setTN(round2(ModbusReader.readFloat(connection,42006)));
            data.setFlow(round2(ModbusReader.readFloat(connection,42040)));
            data.setVelocity(round2(ModbusReader.readFloat(connection,42042)));
            data.setCumulativeFlow(round2(ModbusReader.readFloat(connection,45004)));

            data.setFilterFeedPumpStatus(ModbusReader.readCoil(connection, 8507+1));

            data.setDateAndTimeOfEffluent(LocalDateTime.now());
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