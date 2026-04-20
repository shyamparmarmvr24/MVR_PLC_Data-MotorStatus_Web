package com.mvrtechnology.plcdata.cache;
import com.mvrtechnology.plcdata.entity.PlantDetails;
import com.mvrtechnology.plcdata.repository.IPlantDetailsRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlantCache {

    private final IPlantDetailsRepo repo;

    private final ConcurrentHashMap<Integer, PlantDetails> cache = new ConcurrentHashMap<>();

    public PlantCache(IPlantDetailsRepo repo) {
        this.repo = repo;
    }

    @PostConstruct
    public void load() {
        refresh();
    }

    public void refresh() {
        cache.clear();
        repo.findAll().forEach(p -> cache.put(p.getPlantId(), p));
    }

    public Collection<PlantDetails> getAll() {
        return cache.values();
    }

    public PlantDetails get(Integer plantId) {
        return cache.get(plantId);
    }
}
