package com.mvrtechnology.plcdata.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlcStatusService
{
    // ONLINE after 2 successful polls
    private static final int SUCCESS_THRESHOLD = 2;

    // OFFLINE after 4 consecutive failures
    private static final int FAILURE_THRESHOLD = 4;

    private final ConcurrentHashMap<Integer,Integer> successCounter =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer,Integer> failureCounter =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer,Boolean> status =
            new ConcurrentHashMap<>();


    public boolean reportSuccess(Integer plantId)
    {
        successCounter.merge(
                plantId,
                1,
                Integer::sum);

        failureCounter.put(
                plantId,
                0);

        if(successCounter.get(plantId) >= SUCCESS_THRESHOLD)
        {
            Boolean previous =
                    status.getOrDefault(
                            plantId,
                            false);

            status.put(
                    plantId,
                    true);

            return !previous;
        }

        return false;
    }


    public boolean reportFailure(Integer plantId)
    {
        failureCounter.merge(
                plantId,
                1,
                Integer::sum);

        successCounter.put(
                plantId,
                0);

        if(failureCounter.get(plantId) >= FAILURE_THRESHOLD)
        {
            Boolean previous =
                    status.getOrDefault(
                            plantId,
                            false);

            status.put(
                    plantId,
                    false);

            return previous;
        }

        return false;
    }


    public boolean isOnline(Integer plantId)
    {
        return status.getOrDefault(
                plantId,
                false);
    }
}