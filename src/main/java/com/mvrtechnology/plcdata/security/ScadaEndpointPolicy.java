package com.mvrtechnology.plcdata.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ScadaEndpointPolicy
{
    /*
     * These endpoints return data for multiple/all plants.
     *
     * PLANT-level IPs are NOT allowed to access them.
     */
    private final Set<String> allPlantEndpoints =
            Set.of(
                    "/api/machinery-status/by-all/plants",
                    "/api/effluent/average/day/all-plants"
            );


    /*
     * These endpoints are not plant-specific.
     *
     * PLANT-level IPs can access them.
     */
    private final Set<String> globalEndpoints =
            Set.of(
                    "/api/health",
                    "/api/events/updates"
            );


    public boolean isAllPlantEndpoint(
            HttpServletRequest request)
    {
        return allPlantEndpoints.contains(
                request.getRequestURI()
        );
    }


    public boolean isGlobalEndpoint(
            HttpServletRequest request)
    {
        return globalEndpoints.contains(
                request.getRequestURI()
        );
    }
}