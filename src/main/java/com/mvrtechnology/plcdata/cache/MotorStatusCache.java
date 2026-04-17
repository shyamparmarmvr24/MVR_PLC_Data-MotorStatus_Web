package com.mvrtechnology.plcdata.cache;
import com.mvrtechnology.plcdata.dtos.MotorStatusDTO;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class MotorStatusCache {

    private final ConcurrentHashMap<Integer, MotorStatusDTO> cache = new ConcurrentHashMap<>();

    public void update(Integer plantId, MotorStatusDTO data) {
        cache.put(plantId, data);
    }

    public MotorStatusDTO get(Integer plantId) {
        return cache.get(plantId);
    }
}