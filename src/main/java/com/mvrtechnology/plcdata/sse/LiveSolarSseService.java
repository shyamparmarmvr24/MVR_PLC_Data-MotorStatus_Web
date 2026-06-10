package com.mvrtechnology.plcdata.sse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LiveSolarSseService
{
    private final ConcurrentHashMap<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Integer plantId)
    {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.put(plantId, emitter);

        emitter.onCompletion(() -> emitters.remove(plantId));
        emitter.onTimeout(() -> emitters.remove(plantId));
        emitter.onError(ex -> emitters.remove(plantId));

        return emitter;
    }

    public Set<Integer> getSubscribedPlants()
    {
        return emitters.keySet();
    }

    public void send(Integer plantId,Object data)
    {
        SseEmitter emitter = emitters.get(plantId);

        if(emitter == null)
        {
            return;
        }

        try
        {
            emitter.send(
                    SseEmitter.event()
                            .name("live-solar")
                            .data(data)
            );
        }
        catch(Exception ex)
        {
            emitter.complete();
            emitters.remove(plantId);
        }
    }

    @Scheduled(fixedRate = 15000)
    public void heartbeat()
    {
        emitters.forEach((plantId, emitter) ->
        {
            try
            {
                emitter.send(
                        SseEmitter.event()
                                .name("heartbeat")
                                .data("ping"));
            }
            catch(Exception ex)
            {
                emitter.complete();
                emitters.remove(plantId);
            }
        });
    }
}