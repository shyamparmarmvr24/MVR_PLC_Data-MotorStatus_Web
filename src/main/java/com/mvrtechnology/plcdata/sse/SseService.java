package com.mvrtechnology.plcdata.sse;
import com.mvrtechnology.plcdata.dtos.AllPlantStatusDTO;
import com.mvrtechnology.plcdata.dtos.PlantMotorResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService
{
    private final Map<Integer, List<SseEmitter>> clients = new ConcurrentHashMap<>();
    private final Map<Integer, List<SseEmitter>> effluentClients = new ConcurrentHashMap<>();
    private final Map<Integer, List<SseEmitter>> solarClients = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Integer plantId, PlantMotorResponseDTO initialData)
    {

        SseEmitter emitter = new SseEmitter(0L);

        clients.computeIfAbsent(plantId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeClient(plantId, emitter));
        emitter.onTimeout(() -> removeClient(plantId, emitter));
        emitter.onError((e) -> removeClient(plantId, emitter));

        try {
            if (initialData != null) {
                emitter.send(initialData);
            }
        } catch (Exception e) {
            emitter.complete();
        }

        return emitter;
    }

    public void send(Integer plantId, Object data)
    {

        List<SseEmitter> emitters = clients.get(plantId);

        if (emitters == null) return;

        for (SseEmitter emitter : emitters)
        {
            try
            {
                emitter.send(SseEmitter.event().name("motor-update").data(data));
            }
            catch (Exception e)
            {
                emitter.complete();
                removeClient(plantId, emitter);
            }
        }
    }

    private void removeClient(Integer plantId, SseEmitter emitter) {
        List<SseEmitter> list = clients.get(plantId);
        if (list != null) list.remove(emitter);
    }

    public void sendPlantInfoUpdate(AllPlantStatusDTO data) {

        List<SseEmitter> emitters = clients.get(0);

        if (emitters == null) return;

        for (SseEmitter emitter : emitters) {
            try
            {
                emitter.send(SseEmitter.event().name("plant-status-update").data(data));
            }
            catch (Exception e)
            {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }

    public SseEmitter subscribeEffluentPlant(Integer plantId, Object initialData) {

        SseEmitter emitter = new SseEmitter(0L);

        effluentClients.computeIfAbsent(plantId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEffluentClient(plantId, emitter));
        emitter.onTimeout(() -> removeEffluentClient(plantId, emitter));
        emitter.onError(e -> removeEffluentClient(plantId, emitter));

        try {
            if (initialData != null) {
                emitter.send(SseEmitter.event().name("init").data(initialData));
            }
        }
        catch (Exception e)
        {
            emitter.complete();
        }

        return emitter;
    }

    public void sendEffluent(Integer plantId, Object data)
    {
        List<SseEmitter> emitters = effluentClients.get(plantId);
        if (emitters == null) return;

        for (SseEmitter emitter : emitters)
        {
            try
            {
                emitter.send(SseEmitter.event().name("effluent-update").data(data));
            }
            catch (Exception e)
            {
                emitter.complete();
                removeEffluentClient(plantId, emitter);
            }
        }
    }

    private void removeEffluentClient(Integer plantId, SseEmitter emitter) {
        List<SseEmitter> list = effluentClients.get(plantId);
        if (list != null) list.remove(emitter);
    }

    public void sendSolarUpdate(
            Integer plantId,
            Object data)
    {
        List<SseEmitter> emitters =
                solarClients.get(plantId);

        if(emitters == null)
        {
            return;
        }

        for(SseEmitter emitter : emitters)
        {
            try
            {
                emitter.send(SseEmitter.event().name("solar-update").data(data));
            }
            catch (Exception e)
            {
                emitter.complete();removeSolarClient(plantId, emitter);
            }
        }
    }

    public SseEmitter subscribeSolar(Integer plantId, Object initialData)
    {
        SseEmitter emitter = new SseEmitter(0L);

        solarClients.computeIfAbsent(plantId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeSolarClient(plantId, emitter));

        emitter.onTimeout(() -> removeSolarClient(plantId, emitter));

        emitter.onError(e -> removeSolarClient(plantId, emitter));

        try
        {
            if(initialData != null)
            {
                emitter.send(
                        SseEmitter.event()
                                .name("init")
                                .data(initialData));
            }
        }
        catch (Exception e)
        {
            emitter.complete();
        }

        return emitter;
    }

    private void removeSolarClient(
            Integer plantId,
            SseEmitter emitter)
    {
        List<SseEmitter> list =
                solarClients.get(plantId);

        if(list != null)
        {
            list.remove(emitter);
        }
    }
}