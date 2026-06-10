package com.mvrtechnology.plcdata.controller;
import com.mvrtechnology.plcdata.dtos.EffluentDailyAverageDTO;
import com.mvrtechnology.plcdata.dtos.EffluentDayDataDTO;
import com.mvrtechnology.plcdata.dtos.PlantEffluentResponseDTO;
import com.mvrtechnology.plcdata.service.IEffluentDataService;
import com.mvrtechnology.plcdata.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/effluent")
@CrossOrigin(origins="*")
public class EffluentController
{
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

    @GetMapping("/day")
    public EffluentDayDataDTO getDayData(@RequestParam Integer plantId, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date, @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime fromTime, @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime toTime)
    {
        return service.getDayData(plantId, date, fromTime, toTime);
    }

    @GetMapping("/average")
    public List<EffluentDailyAverageDTO> getAverage(@RequestParam Integer plantId, @RequestParam @DateTimeFormat(pattern="dd-MM-yyyy") LocalDate fromDate, @RequestParam @DateTimeFormat(pattern="dd-MM-yyyy") LocalDate toDate)
    {
        return service.getDateRangeAverage(plantId, fromDate, toDate);
    }
}