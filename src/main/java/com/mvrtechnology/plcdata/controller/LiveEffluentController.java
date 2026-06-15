package com.mvrtechnology.plcdata.controller;
import com.mvrtechnology.plcdata.sse.LiveEffluentSseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/effluent/live")
@CrossOrigin(origins="*")
public class LiveEffluentController
{
    @Autowired
    private LiveEffluentSseService sseService;

    @GetMapping("/{plantId}")
    public SseEmitter streamLive(
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
            emitter.complete();
        }

        return emitter;
    }
}