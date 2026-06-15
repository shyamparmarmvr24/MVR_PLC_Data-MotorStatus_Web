package com.mvrtechnology.plcdata.cache;
import com.mvrtechnology.plcdata.dtos.MotorStatusDTO;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class MotorStatusCache {

    private final ConcurrentHashMap<Integer, MotorStatusDTO> cache = new ConcurrentHashMap<>();

    public void update(Integer plantId, MotorStatusDTO data) {
        cache.put(plantId, data);
    }

    public MotorStatusDTO get(Integer plantId) {
        return cache.get(plantId);
    }

//    public MotorStatusDTO get(Integer plantId)
//    {
//        return cache.getOrDefault(
//                plantId,
//                createOfflineStatus());
//    }

//    private MotorStatusDTO createOfflineStatus()
//    {
//        MotorStatusDTO dto = new MotorStatusDTO();
//
//        dto.setEmg(false);
//        dto.setSm400(false);
//
//        dto.setTreatedWaterPumpOnOf(false);
//        dto.setSludgeSubPumpOnOf(false);
//        dto.setPyroExhaustFanOnOf(false);
//        dto.setPyroBlowerOnOf(false);
//        dto.setPolyDosingOnOf(false);
//        dto.setNonSubPumpOnOf(false);
//        dto.setMcCleaningOnOf(false);
//        dto.setFilterFeedPumpOnOf(false);
//        dto.setCheDosPumpOnOf(false);
//        dto.setBlowerOnOf(false);
//        dto.setWaterSubPumpOnOf(false);
//        dto.setUnderWaterPumpOnOf(false);
//        dto.setWaterBoosterPumpOnOf(false);
//
//        dto.setSrs1OnOf(false);
//        dto.setSrs2OnOf(false);
//        dto.setPolymerMixerOnOf(false);
//        dto.setDewateringMixerOnOf(false);
//        dto.setDewateringScrew1_2OnOf(false);
//        dto.setScrewConveyor1OnOf(false);
//        dto.setDryerOnOf(false);
//        dto.setDucting1_2OnOf(false);
//        dto.setScrewConveyor2OnOf(false);
//        dto.setPyro1OnOf(false);
//        dto.setPyro2OnOf(false);
//
//        dto.setSubmersiblePumpTrip(false);
//        dto.setFilterFeedPumpTrip(false);
//        dto.setChemicalDosingPumpTrip(false);
//        dto.setPyrolyzerExhaustFanTrip(false);
//        dto.setNonSubmersiblePumpTrip(false);
//        dto.setPyrolyzerBlowerFanTrip(false);
//        dto.setPolymerDosingMotorTrip(false);
//        dto.setBlowerMotorTrip(false);
//        dto.setSrs1MotorTrip(false);
//        dto.setSrs2MotorTrip(false);
//        dto.setDewateringMixerTrip(false);
//        dto.setDewateringScrewPress1Trip(false);
//        dto.setPolymerMixerTrip(false);
//        dto.setScrewConveyor1Trip(false);
//        dto.setScrewConveyor2Trip(false);
//        dto.setDryerMotorTrip(false);
//        dto.setPyrolizerMotor1Trip(false);
//        dto.setPyrolizerMotor2Trip(false);
//        dto.setDuctingMotor1Trip(false);
//        dto.setDuctingMotor2Trip(false);
//        dto.setTreatedWaterPumpTrip(false);
//        dto.setPressurePumpTrip(false);
//        dto.setEmergencyBottonPressed(false);
//        dto.setDeWateringScrewPress2Trip(false);
//
//        dto.setHotWaterOutlet(0.0);
//        dto.setHotWaterInlet(0.0);
//        dto.setExhaustChimney(0.0);
//        dto.setPyrolyzerMiddle(0.0);
//        dto.setPyrolyzerBottom(0.0);
//        dto.setScreenChamberLevel(0.0);
//
//        return dto;
//    }
}