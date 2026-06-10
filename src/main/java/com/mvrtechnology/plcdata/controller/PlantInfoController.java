package com.mvrtechnology.plcdata.controller;
import com.mvrtechnology.plcdata.dtos.PlantInfoDto;
import com.mvrtechnology.plcdata.service.PlantInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/plants")
@CrossOrigin(origins="*")
public class PlantInfoController
{
    @Autowired
    private PlantInfoService service;

    @GetMapping
    public List<PlantInfoDto> getAllPlants() {
        return service.getAllPlants();
    }
}