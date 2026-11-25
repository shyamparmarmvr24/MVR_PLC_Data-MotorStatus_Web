package com.mvrtechnology.plcdata.controller;
import com.mvrtechnology.plcdata.dtos.PlantDetailsResponseDTO;
import com.mvrtechnology.plcdata.dtos.PlantInfoDto;
import com.mvrtechnology.plcdata.service.IPlantDetailsService;
import com.mvrtechnology.plcdata.service.PlantInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/plants")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PlantInfoController
{
    private final PlantInfoService service;

    private final IPlantDetailsService plantService;


    @GetMapping
    public List<PlantInfoDto> getAllPlants(
            HttpServletRequest request)
    {
        Object accessType =
                request.getAttribute(
                        "SCADA_ACCESS_TYPE"
                );


        if ("ALL".equals(
                String.valueOf(accessType)))
        {
            return service.getAllPlants(request);
        }


        @SuppressWarnings("unchecked")
        Set<Integer> allowedPlantIds =
                (Set<Integer>) request.getAttribute(
                        "SCADA_ALLOWED_PLANT_IDS"
                );


        return service.getPlantsForAccess(
                allowedPlantIds
        );
    }



    @GetMapping("/{plantId}")
    public ResponseEntity<PlantDetailsResponseDTO>
    getPlantDetails(
            @PathVariable Integer plantId)
    {
        return ResponseEntity.ok(
                plantService.getPlantDetails(
                        plantId
                )
        );
    }
}