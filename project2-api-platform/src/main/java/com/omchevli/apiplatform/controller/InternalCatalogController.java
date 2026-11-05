package com.omchevli.apiplatform.controller;

import com.omchevli.apiplatform.model.CatalogItem;
import com.omchevli.apiplatform.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Internal API: full records (including internalCost) and write access.
 * Protected by {@link com.omchevli.apiplatform.security.ApiKeyFilter} (X-Api-Key header).
 */
@RestController
@RequestMapping("/api/internal/v1/items")
public class InternalCatalogController {

    private final CatalogService service;

    public InternalCatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<CatalogItem> list(@RequestParam(required = false) String category) {
        return service.list(category);
    }

    @GetMapping("/{id}")
    public CatalogItem get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogItem create(@Valid @RequestBody CatalogItem item) {
        return service.create(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
