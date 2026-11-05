package com.omchevli.apiplatform.controller;

import com.omchevli.apiplatform.dto.PublicItemDto;
import com.omchevli.apiplatform.service.CatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Versioned public API for web/mobile clients. Returns public DTOs only. */
@RestController
@RequestMapping("/api/public/v1/items")
public class PublicCatalogController {

    private final CatalogService service;

    public PublicCatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<PublicItemDto> list(@RequestParam(required = false) String category) {
        return service.list(category).stream().map(PublicItemDto::from).toList();
    }

    @GetMapping("/{id}")
    public PublicItemDto get(@PathVariable String id) {
        return PublicItemDto.from(service.get(id));
    }
}
