package com.mvrtechnology.plcdata.controller;
import com.mvrtechnology.plcdata.dtos.PlantEffluentResponseDTO;
import com.mvrtechnology.plcdata.service.IEffluentDataService;
import com.mvrtechnology.plcdata.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/effluent")
@CrossOrigin(origins="*")
public class EffluentController {

    @Autowired
    private IEffluentDataService service;

    @Autowired
    private SseService sseService;

    @GetMapping("/data/{plantId}")
    public SseEmitter streamPlant(@PathVariable Integer plantId)
    {
        PlantEffluentResponseDTO initial = service.getLatestByPlant(plantId);
        return sseService.subscribeEffluentPlant(plantId, initial);
    }
}
