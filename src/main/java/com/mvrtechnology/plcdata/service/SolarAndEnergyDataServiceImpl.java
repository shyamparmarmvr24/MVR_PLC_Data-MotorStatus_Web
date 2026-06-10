package com.mvrtechnology.plcdata.service;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.PlantSolarResponseDTO;
import com.mvrtechnology.plcdata.dtos.SolarAndEnergyDateRangeSummaryDTO;
import com.mvrtechnology.plcdata.dtos.SolarDataResponseDTO;
import com.mvrtechnology.plcdata.dtos.SolarDayWiseResponseDTO;
import com.mvrtechnology.plcdata.entity.*;
import com.mvrtechnology.plcdata.repository.IPlantDetailsRepo;
import com.mvrtechnology.plcdata.repository.ISolarAndEnergyDataRepo;
import com.mvrtechnology.plcdata.repository.ISolarAndEnergyDataRepoImpl;
import com.mvrtechnology.plcdata.util.ModbusReader;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class SolarAndEnergyDataServiceImpl implements ISolarAndEnergyDataService
{
    @Autowired
    private ISolarAndEnergyDataRepo solarRepo;

    @Autowired
    private PlantCache plantCache;

    @Autowired
    private ISolarAndEnergyDataRepoImpl solarRepoImpl;

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

            if (data != null)
            {
                return solarRepo.save(data);
            }
            return null;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public PlantSolarResponseDTO getLatestByPlant(Integer plantId)
    {

        PlantDetails plant = plantCache.get(plantId);

        if(plant == null)
        {
            throw new RuntimeException("Plant Not Found");
        }

        SolarAndEnergyData solar = solarRepo.getLatestData(plantId);

        if (solar == null)
        {
            return null;
        }

        SolarDataResponseDTO solarDTO = new SolarDataResponseDTO();

        BeanUtils.copyProperties(solar, solarDTO);

        PlantSolarResponseDTO response = new PlantSolarResponseDTO();

        response.setPlantId(plant.getPlantId());

        response.setPlantName(plant.getPlantName());

        response.setZone(plant.getZone());

        response.setSolarData(solarDTO);

        return response;
    }

    @Override
    public SolarDayWiseResponseDTO getHistoryData(Integer plantId, LocalDate date, LocalTime fromTime, LocalTime toTime) {
        return solarRepoImpl.getHistoryData(plantId,date,fromTime,toTime);
    }

    @Override
    public List<SolarAndEnergyDateRangeSummaryDTO> getSolarSummary(Integer plantId, LocalDate fromDate, LocalDate toDate) {
        return solarRepoImpl.getSolarSummary(plantId,fromDate,toDate);
    }

    private BigDecimal round2(double val)
    {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
    }
}