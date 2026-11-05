package com.omchevli.apiplatform.repository;

import com.omchevli.apiplatform.model.CatalogItem;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository {
    List<CatalogItem> findAll();
    Optional<CatalogItem> findById(String id);
    CatalogItem save(CatalogItem item);
    void deleteById(String id);
}
