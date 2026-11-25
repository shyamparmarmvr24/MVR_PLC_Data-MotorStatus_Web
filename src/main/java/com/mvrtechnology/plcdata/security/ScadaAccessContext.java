package com.mvrtechnology.plcdata.security;

import com.mvrtechnology.plcdata.enums.ScadaAccessType;

import java.util.Set;

public record ScadaAccessContext(
        Long accessId,
        String ipAddress,
        ScadaAccessType accessType,
        Set<Integer> plantIds
)
{
    public boolean hasAllAccess()
    {
        return accessType == ScadaAccessType.ALL;
    }


    public boolean hasPlantAccess()
    {
        return accessType == ScadaAccessType.PLANT;
    }


    public boolean canAccessPlant(
            Integer requestedPlantId)
    {
        if (requestedPlantId == null)
        {
            return false;
        }


        if (hasAllAccess())
        {
            return true;
        }


        return hasPlantAccess()
                && plantIds != null
                && plantIds.contains(
                requestedPlantId
        );
    }
}