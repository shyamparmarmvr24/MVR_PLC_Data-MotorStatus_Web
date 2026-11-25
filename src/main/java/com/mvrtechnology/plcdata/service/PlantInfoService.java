package com.mvrtechnology.plcdata.service;

import com.mvrtechnology.plcdata.cache.MotorStatusCache;
import com.mvrtechnology.plcdata.cache.PlantCache;
import com.mvrtechnology.plcdata.dtos.PlantInfoDto;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.security.ClientIpResolver;
import com.mvrtechnology.plcdata.security.ScadaAccessContext;
import com.mvrtechnology.plcdata.security.ScadaAuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlantInfoService
{
    private final PlantCache plantCache;

    private final MotorStatusCache motorCache;

    private final PlcStatusService plcStatusService;

    private final ClientIpResolver clientIpResolver;

    private final ScadaAuthorizationService authorizationService;


    public List<PlantInfoDto> getAllPlants(
            HttpServletRequest request)
    {
        List<PlantInfoDto> response =
                new ArrayList<>();


        String clientIp =
                clientIpResolver.resolve(
                        request
                );


        ScadaAccessContext access =
                authorizationService.getAccess(
                        clientIp
                );


        boolean allAccess =
                access.hasAllAccess();


        Set<Integer> allowedPlantIds =
                access.plantIds();


        for (PlantDetails plant :
                plantCache.getAll())
        {
            /*
             * Restricted IP:
             *
             * Return only assigned plants.
             */
            if (!allAccess)
            {
                if (allowedPlantIds == null ||
                        !allowedPlantIds.contains(
                                plant.getPlantId()
                        ))
                {
                    continue;
                }
            }


            PlantInfoDto dto =
                    new PlantInfoDto();


            dto.setPlantId(
                    plant.getPlantId()
            );

            dto.setPlantName(
                    plant.getPlantName()
            );

            dto.setZone(
                    plant.getZone()
            );

            dto.setDistrict(
                    plant.getDistrict()
            );

            dto.setPlantKLD(
                    plant.getPlantKLD()
            );

            dto.setIsMNITCompleted(
                    plant.getIsMNITCompleted()
            );

            dto.setMnitCompletionDate(
                    plant.getMnitCompletionDate()
            );

            dto.setIsSolarCompleted(
                    plant.getIsSolarCompleted()
            );

            dto.setSolarCompletionDate(
                    plant.getSolarCompletionDate()
            );

            dto.setLatitude(
                    plant.getLatitude()
            );

            dto.setLongitude(
                    plant.getLongitude()
            );


            dto.setPlcStatusSM400(
                    plcStatusService.isOnline(
                            plant.getPlantId()
                    )
            );


            response.add(dto);
        }


        return response;
    }

    public List<PlantInfoDto> getPlantsForAccess(
            Set<Integer> allowedPlantIds)
    {
        List<PlantInfoDto> response =
                new ArrayList<>();


        if (allowedPlantIds == null ||
                allowedPlantIds.isEmpty())
        {
            return response;
        }


        for (PlantDetails plant :
                plantCache.getAll())
        {
            if (!allowedPlantIds.contains(
                    plant.getPlantId()))
            {
                continue;
            }


            PlantInfoDto dto =
                    new PlantInfoDto();


            dto.setPlantId(
                    plant.getPlantId()
            );

            dto.setPlantName(
                    plant.getPlantName()
            );

            dto.setZone(
                    plant.getZone()
            );

            dto.setDistrict(
                    plant.getDistrict()
            );

            dto.setPlantKLD(
                    plant.getPlantKLD()
            );

            dto.setIsMNITCompleted(
                    plant.getIsMNITCompleted()
            );

            dto.setMnitCompletionDate(
                    plant.getMnitCompletionDate()
            );

            dto.setIsSolarCompleted(
                    plant.getIsSolarCompleted()
            );

            dto.setSolarCompletionDate(
                    plant.getSolarCompletionDate()
            );

            dto.setLatitude(
                    plant.getLatitude()
            );

            dto.setLongitude(
                    plant.getLongitude()
            );


            dto.setPlcStatusSM400(
                    plcStatusService.isOnline(
                            plant.getPlantId()
                    )
            );


            response.add(dto);
        }


        return response;
    }
}