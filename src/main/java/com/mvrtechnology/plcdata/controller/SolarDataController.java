package com.mvrtechnology.plcdata.controller;
import com.mvrtechnology.plcdata.dtos.PlantSolarResponseDTO;
import com.mvrtechnology.plcdata.dtos.SolarAndEnergyDateRangeSummaryDTO;
import com.mvrtechnology.plcdata.dtos.SolarDayWiseResponseDTO;
import com.mvrtechnology.plcdata.service.ISolarAndEnergyDataService;
import com.mvrtechnology.plcdata.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/solar")
@CrossOrigin(origins = "*")
public class SolarDataController
{
    @Autowired
    private ISolarAndEnergyDataService solarService;
    @Autowired
    private SseService sseService;

    @GetMapping("/{plantId}")
    public PlantSolarResponseDTO getLatestData(@PathVariable Integer plantId)
    {
        return solarService.getLatestByPlant(plantId);
    }

    @GetMapping("/sse/{plantId}")
    public SseEmitter subscribeSolar(@PathVariable Integer plantId)
    {
        PlantSolarResponseDTO response = solarService.getLatestByPlant(plantId);

        return sseService.subscribeSolar(plantId, response);
    }

    @GetMapping("/day-wise")
    public SolarDayWiseResponseDTO getHistory(@RequestParam Integer plantId, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
                                              @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime fromTime,
                                              @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime toTime)
    {
        return solarService.getHistoryData(plantId, date, fromTime, toTime);
    }

    @GetMapping("/date-range")
    public List<SolarAndEnergyDateRangeSummaryDTO> getSummary(@RequestParam Integer plantId, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDate,
                                                              @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDate)
    {
        return solarService.getSolarSummary(plantId, fromDate, toDate);
    }
}