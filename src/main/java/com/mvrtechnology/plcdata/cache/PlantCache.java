package com.mvrtechnology.plcdata.cache;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.repository.IPlantDetailsRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlantCache
{
    @Autowired
    private IPlantDetailsRepo repo;

    private final ConcurrentHashMap<Integer, PlantDetails> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void load() {
        refresh();
    }

    @Scheduled(fixedDelay = 300000)
    public void autoRefresh()
    {
        refresh();
    }

    public void refresh()
    {
        ConcurrentHashMap<Integer,PlantDetails>
                temp =
                new ConcurrentHashMap<>();

        repo.findAll().forEach(
                p -> temp.put(
                        p.getPlantId(),
                        p));

        cache.clear();

        cache.putAll(temp);
    }
    public Collection<PlantDetails> getAll() {
        return cache.values();
    }

    public PlantDetails get(Integer plantId) {
        return cache.get(plantId);
    }
}
