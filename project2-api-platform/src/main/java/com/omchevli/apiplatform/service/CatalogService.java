package com.omchevli.apiplatform.service;

import com.omchevli.apiplatform.model.CatalogItem;
import com.omchevli.apiplatform.repository.CatalogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CatalogService {

    private final CatalogRepository repository;

    public CatalogService(CatalogRepository repository) {
        this.repository = repository;
    }

    public List<CatalogItem> list(String category) {
        List<CatalogItem> all = repository.findAll();
        if (category == null || category.isBlank()) {
            return all;
        }
        return all.stream()
                .filter(i -> i.category().equalsIgnoreCase(category))
                .toList();
    }

    public CatalogItem get(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found: " + id));
    }

    public CatalogItem create(CatalogItem item) {
        if (item.price() == null || item.price().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be zero or positive");
        }
        return repository.save(item);
    }

    public void delete(String id) {
        get(id); // 404 if absent
        repository.deleteById(id);
    }
}
