package com.mvrtechnology.plcdata.controller;
import com.mvrtechnology.plcdata.dtos.TankLevelGraphResponseDTO;
import com.mvrtechnology.plcdata.dtos.TankLevelLatestResponseDTO;
import com.mvrtechnology.plcdata.service.ITankLevelDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tank-level")
@CrossOrigin(origins = "*")
public class TankLevelGraphController
{

    @Autowired
    private ITankLevelDataService tankLevelGraphService;


    @GetMapping("/day-wise/graph")
    public ResponseEntity<TankLevelGraphResponseDTO> getTankLevelGraph(

            @RequestParam Integer plantId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDateTime,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDateTime,

            @RequestParam(defaultValue = "15")
            Integer intervalMinutes,

            @RequestParam(defaultValue = "AVERAGE")
            String aggregationType

    ) {

        return ResponseEntity.ok(
                tankLevelGraphService.getTankLevelGraph(
                        plantId,
                        fromDateTime,
                        toDateTime,
                        intervalMinutes,
                        aggregationType
                )
        );
    }

    @GetMapping("/latest")
    public ResponseEntity<TankLevelLatestResponseDTO> getLatestTankLevel(
            @RequestParam Integer plantId
    ) {

        return ResponseEntity.ok(
                tankLevelGraphService.getLatestTankLevel(plantId)
        );
    }
}
