package com.mvrtechnology.plcdata.controller;
import com.mvrtechnology.plcdata.cache.MotorStatusCache;
import com.mvrtechnology.plcdata.dtos.PlantMotorResponseDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.repository.IPlantDetailsRepo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/motor")
@CrossOrigin(origins="*")
public class MotorController {

    private final MotorStatusCache cache;
    private final IPlantDetailsRepo plantRepo;

    public MotorController(MotorStatusCache cache, IPlantDetailsRepo plantRepo)
    {
        this.cache = cache;
        this.plantRepo = plantRepo;
    }

    @GetMapping("/{plantId}")
    public PlantMotorResponseDTO getMotorData(@PathVariable Integer plantId)
    {
        PlantDetails plant = plantRepo.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant Not Found"));

        PlantMotorResponseDTO response = new PlantMotorResponseDTO();

        response.setPlantId(plant.getPlantId());
        response.setPlantName(plant.getPlantName());
        response.setZone(plant.getZone());

        response.setMotorStatus(cache.get(plantId));

        return response;
    }
}