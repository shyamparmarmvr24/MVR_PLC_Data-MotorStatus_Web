package com.mvrtechnology.plcdata.controller;
import com.mvrtechnology.plcdata.cache.MotorStatusCache;
import com.mvrtechnology.plcdata.dtos.PlantMotorResponseDTO;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.repository.IPlantDetailsRepo;
import com.mvrtechnology.plcdata.sse.SseService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@CrossOrigin(origins="*")
public class SseController
{
    private final SseService sseService;
    private final MotorStatusCache cache;
    private final IPlantDetailsRepo plantRepo;

    public SseController(SseService sseService, MotorStatusCache cache, IPlantDetailsRepo plantRepo)
    {
        this.sseService = sseService;
        this.cache = cache;
        this.plantRepo = plantRepo;
    }

    @GetMapping("/{plantId}")
    public SseEmitter subscribe(@PathVariable Integer plantId) {

        PlantDetails plant = plantRepo.findById(plantId).orElseThrow(() -> new RuntimeException("Plant Not Found"));

        PlantMotorResponseDTO response = new PlantMotorResponseDTO();

        response.setPlantId(plant.getPlantId());
        response.setPlantName(plant.getPlantName());
        response.setZone(plant.getZone());
        response.setMotorStatus(cache.get(plantId));

        return sseService.subscribe(plantId, response);
    }

    @GetMapping("/plants")
    public SseEmitter subscribePlants()
    {
        return sseService.subscribe(0, null);
    }
}
