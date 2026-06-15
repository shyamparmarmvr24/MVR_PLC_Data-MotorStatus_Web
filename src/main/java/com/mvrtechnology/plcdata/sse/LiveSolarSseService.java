package com.mvrtechnology.plcdata.sse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class LiveSolarSseService
{
    private final ConcurrentHashMap<Integer, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Integer plantId)
    {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.computeIfAbsent(plantId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeEmitter(plantId, emitter));

        emitter.onTimeout(() -> removeEmitter(plantId, emitter));

        emitter.onError(ex -> removeEmitter(plantId, emitter));

        return emitter;
    }

    public Set<Integer> getSubscribedPlants()
    {
        return emitters.keySet();
    }

    public void send(
            Integer plantId,
            Object data)
    {
        CopyOnWriteArrayList<SseEmitter>
                list =
                emitters.get(plantId);

        if(list == null)
        {
            return;
        }

        for(SseEmitter emitter : list)
        {
            try
            {
                emitter.send(
                        SseEmitter.event()
                                .name("live-solar")
                                .data(data));
            }
            catch(Exception ex)
            {
                emitter.complete();
                list.remove(emitter);
            }
        }
    }

    @Scheduled(fixedRate = 15000)
    public void heartbeat()
    {
        emitters.forEach((plantId, list) ->
        {
            for(SseEmitter emitter : list)
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
                    removeEmitter(
                            plantId,
                            emitter);
                }
            }
        });
    }

    private void removeEmitter(
            Integer plantId,
            SseEmitter emitter)
    {
        CopyOnWriteArrayList<SseEmitter>
                list =
                emitters.get(plantId);

        if(list != null)
        {
            list.remove(emitter);

            if(list.isEmpty())
            {
                emitters.remove(plantId);
            }
        }
    }
}