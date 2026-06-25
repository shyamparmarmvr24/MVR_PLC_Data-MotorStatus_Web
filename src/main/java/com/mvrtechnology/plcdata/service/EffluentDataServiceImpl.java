package com.mvrtechnology.plcdata.service;
import com.mvrtechnology.plcdata.dtos.EffluentDailyAverageDTO;
import com.mvrtechnology.plcdata.dtos.EffluentDayDataDTO;
import com.mvrtechnology.plcdata.dtos.PlantEffluentResponseDTO;
import com.mvrtechnology.plcdata.entity.*;
import com.mvrtechnology.plcdata.repository.IEffluentDataRepo;
import com.mvrtechnology.plcdata.repository.IEffluentDataRepoImpl;
import com.mvrtechnology.plcdata.util.ModbusReader;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class EffluentDataServiceImpl implements IEffluentDataService
{
    @Autowired
    private IEffluentDataRepoImpl effluentRepo;

    @Autowired
    private IEffluentDataRepo eRepo;

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

            data.setFilterFeedPumpStatus(ModbusReader.readCoil(connection, 8405));

            data.setDateAndTimeOfEffluent(LocalDateTime.now());
            data.setOperationDate(LocalDate.now());

            //System.out.println("Filter Feed "+data.getFilterFeedPumpStatus()+"  Flow "+data.getFlow() + "Plant Id "+data.getPlantDetails().getPlantId());

            if (Boolean.TRUE.equals(data.getFilterFeedPumpStatus()))
            {
                if (!isAllZero(data) && data.getFlow() != null && data.getFlow().compareTo(BigDecimal.valueOf(0.5)) >= 0)
                {
                    return eRepo.save(data);
                }
            }
            return null;
        }
        catch (Exception e)
        {
//            log.error(
//                    "Effluent read failed for plant {}",
//                    plant.getPlantId(),
//                    e);

            return null;
        }
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

    @Override
    public PlantEffluentResponseDTO getLatestByPlant(Integer plantId) {
        return effluentRepo.getLatestByPlant(plantId);
    }

    @Override
    public EffluentDayDataDTO getDayData(Integer plantId, LocalDate date, LocalTime fromTime, LocalTime toTime) {
        return effluentRepo.getDayData(plantId,date,fromTime,toTime);
    }

    @Override
    public List<EffluentDailyAverageDTO> getDateRangeAverage(Integer plantId, LocalDate fromDate, LocalDate toDate) {
        return effluentRepo.getDateRangeAverage(plantId,fromDate,toDate);
    }

    private boolean isZero(BigDecimal val)
    {
        return val == null || val.compareTo(BigDecimal.ZERO) == 0;
    }

    private BigDecimal round2(double val)
    {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
    }
}