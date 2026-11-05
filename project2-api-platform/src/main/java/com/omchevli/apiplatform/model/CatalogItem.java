package com.omchevli.apiplatform.model;

import java.math.BigDecimal;

/** Internal catalog record. {@code internalCost} must never reach public clients. */
public record CatalogItem(
        String id,
        String name,
        String category,
        BigDecimal price,
        BigDecimal internalCost,
        int stock
) {}
