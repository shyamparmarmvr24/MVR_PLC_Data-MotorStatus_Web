package com.mvrtechnology.plcdata.controller;
import com.mvrtechnology.plcdata.dtos.PlantInfoDto;
import com.mvrtechnology.plcdata.service.PlantInfoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/plants")
@CrossOrigin(origins="*")
public class PlantInfoController
{

    private final PlantInfoService service;

    public PlantInfoController(PlantInfoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PlantInfoDto> getAllPlants() {
        return service.getAllPlants();
    }
}