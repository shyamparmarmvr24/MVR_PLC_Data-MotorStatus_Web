package com.mvrtechnology.plcdata.service;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.mvrtechnology.plcdata.dtos.LiveEffluentDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.util.ModbusReader;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class LiveEffluentService
{
    public LiveEffluentDTO readLiveData(PlantDetails plant, TCPMasterConnection connection) throws Exception
    {
        LiveEffluentDTO dto = new LiveEffluentDTO();

        dto.setPlantId(plant.getPlantId());

        dto.setPH(round2(ModbusReader.readFloat(connection,42004)));
        dto.setCod(round2(ModbusReader.readFloat(connection,40402)));
        dto.setBod(round2(ModbusReader.readFloat(connection,40406)));
        dto.setTss(round2(ModbusReader.readFloat(connection,40408)));

        dto.setTemperature(round2(ModbusReader.readWord(connection,41000)*0.1));

        dto.setTN(round2(ModbusReader.readFloat(connection,42006)));
        dto.setFlow(round2(ModbusReader.readFloat(connection,42040)));
        dto.setVelocity(round2(ModbusReader.readFloat(connection,42042)));
        dto.setCumulativeFlow(round2(ModbusReader.readFloat(connection,45004)));

        dto.setFilterFeedPumpStatus(ModbusReader.readCoil(connection,8406));

        dto.setTimeStamp(LocalDateTime.now());

        return dto;
    }

    private BigDecimal round2(double val)
    {
        return BigDecimal.valueOf(val)
                .setScale(2, RoundingMode.HALF_UP);
    }
}