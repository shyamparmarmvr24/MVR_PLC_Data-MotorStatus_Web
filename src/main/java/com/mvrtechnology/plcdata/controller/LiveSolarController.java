package com.mvrtechnology.plcdata.controller;

import com.mvrtechnology.plcdata.sse.LiveSolarSseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/solar/live")
@CrossOrigin(origins="*")
@Slf4j
public class LiveSolarController
{
    @Autowired
    private LiveSolarSseService sseService;

    @GetMapping("/{plantId}")
    public SseEmitter stream(
            @PathVariable Integer plantId)
    {
        SseEmitter emitter =
                sseService.subscribe(plantId);

        try
        {
            emitter.send(
                    SseEmitter.event()
                            .name("connected")
                            .data("connected"));
        }
        catch(Exception ex)
        {
            //emitter.complete();
            log.warn("Live Solar Failed To Read "+ex);
        }

        return emitter;
    }
}
