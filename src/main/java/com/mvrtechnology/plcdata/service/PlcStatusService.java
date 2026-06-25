package com.mvrtechnology.plcdata.service;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlcStatusService
{
    private final ConcurrentHashMap<Integer, Boolean>
            plcOnlineStatus = new ConcurrentHashMap<>();

    public void updateStatus(
            Integer plantId,
            boolean online)
    {
        plcOnlineStatus.put(plantId, online);
    }

    public boolean isOnline(Integer plantId)
    {
        return plcOnlineStatus.getOrDefault(
                plantId,
                false);
    }
}
