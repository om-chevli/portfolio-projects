package com.omchevli.apiplatform.repository;

import com.omchevli.apiplatform.model.CatalogItem;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory catalog, seeded with sample data so the API is explorable immediately. */
@Repository
public class InMemoryCatalogRepository implements CatalogRepository {

    private final Map<String, CatalogItem> store = new ConcurrentHashMap<>();

    public InMemoryCatalogRepository() {
        save(new CatalogItem("kb-001", "Mechanical Keyboard", "peripherals",
                new BigDecimal("129.99"), new BigDecimal("74.50"), 42));
        save(new CatalogItem("mn-002", "27in Monitor", "displays",
                new BigDecimal("329.00"), new BigDecimal("210.00"), 0));
        save(new CatalogItem("hs-003", "Noise-Cancelling Headset", "audio",
                new BigDecimal("199.50"), new BigDecimal("121.25"), 17));
    }

    @Override
    public List<CatalogItem> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public Optional<CatalogItem> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public CatalogItem save(CatalogItem item) {
        store.put(item.id(), item);
        return item;
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
