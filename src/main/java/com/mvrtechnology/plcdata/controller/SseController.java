//package com.mvrtechnology.plcdata.controller;
//import com.mvrtechnology.plcdata.cache.MotorStatusCache;
//import com.mvrtechnology.plcdata.dtos.PlantMotorResponseDTO;
//import com.mvrtechnology.plcdata.entity.PlantDetails;
//import com.mvrtechnology.plcdata.repository.IPlantDetailsRepo;
//import com.mvrtechnology.plcdata.sse.SseService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//@RestController
//@RequestMapping("/api/sse")
//@CrossOrigin(origins="*")
//public class SseController
//{
//    @Autowired
//    private SseService sseService;
//    @Autowired
//    private MotorStatusCache cache;
//    @Autowired
//    private IPlantDetailsRepo plantRepo;
//
//
//    @GetMapping("/machinery-status/{plantId}")
//    public SseEmitter subscribe(@PathVariable Integer plantId) {
//
//        PlantDetails plant = plantRepo.findById(plantId).orElseThrow(() -> new RuntimeException("Plant Not Found"));
//
//        PlantMotorResponseDTO response = new PlantMotorResponseDTO();
//
//        response.setPlantId(plant.getPlantId());
//        response.setPlantName(plant.getPlantName());
//        response.setZone(plant.getZone());
//        response.setMotorStatus(cache.get(plantId));
//
//        return sseService.subscribe(plantId, response);
//    }
//
//    @GetMapping("/plants")
//    public SseEmitter subscribePlants()
//    {
//        return sseService.subscribe(0, null);
//    }
//}
