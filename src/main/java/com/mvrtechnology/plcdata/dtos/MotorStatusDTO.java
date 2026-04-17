package com.mvrtechnology.plcdata.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MotorStatusDTO
{
    private Boolean emg;
    private Boolean sm400;

    private Boolean treatedWaterPumpOnOf;
    private Boolean sludgeSubPumpOnOf;
    private Boolean pyroExhaustFanOnOf;
    private Boolean pyroBlowerOnOf;
    private Boolean polyDosingOnOf;
    private Boolean nonSubPumpOnOf;
    private Boolean mcCleaningOnOf;
    private Boolean filterFeedPumpOnOf;
    private Boolean cheDosPumpOnOf;
    private Boolean blowerOnOf;
    private Boolean waterSubPumpOnOf;
    private Boolean underWaterPumpOnOf;
    private Boolean waterBoosterPumpOnOf;
    private Boolean srs1OnOf;
    private Boolean srs2OnOf;
    private Boolean polymerMixerOnOf;
    private Boolean dewateringMixerOnOf;
    private Boolean dewateringScrew1_2OnOf;
    private Boolean screwConveyor1OnOf;
    private Boolean dryerOnOf;
    private Boolean ducting1_2OnOf;
    private Boolean screwConveyor2OnOf;
    private Boolean pyro1OnOf;
    private Boolean pyro2OnOf;

    private Boolean submersiblePumpTrip;
    private Boolean filterFeedPumpTrip;
    private Boolean chemicalDosingPumpTrip;
    private Boolean pyrolyzerExhaustFanTrip;
    private Boolean nonSubmersiblePumpTrip;
    private Boolean pyrolyzerBlowerFanTrip;
    private Boolean polymerDosingMotorTrip;
    private Boolean blowerMotorTrip;
    private Boolean srs1MotorTrip;
    private Boolean srs2MotorTrip;
    private Boolean dewateringMixerTrip;
    private Boolean dewateringScrewPress1Trip;
    private Boolean polymerMixerTrip;
    private Boolean screwConveyor1Trip;
    private Boolean screwConveyor2Trip;
    private Boolean dryerMotorTrip;
    private Boolean pyrolizerMotor1Trip;
    private Boolean pyrolizerMotor2Trip;
    private Boolean ductingMotor1Trip;
    private Boolean ductingMotor2Trip;
    private Boolean treatedWaterPumpTrip;
    private Boolean pressurePumpTrip;
    private Boolean emergencyBottonPressed;
    private Boolean deWateringScrewPress2Trip;
}