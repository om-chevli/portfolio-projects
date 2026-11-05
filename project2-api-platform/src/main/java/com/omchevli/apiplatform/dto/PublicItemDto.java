package com.omchevli.apiplatform.dto;

import com.omchevli.apiplatform.model.CatalogItem;

import java.math.BigDecimal;

/** Public view of a catalog item: no internalCost, no stock internals beyond availability. */
public record PublicItemDto(
        String id,
        String name,
        String category,
        BigDecimal price,
        boolean inStock
) {
    public static PublicItemDto from(CatalogItem item) {
        return new PublicItemDto(item.id(), item.name(), item.category(), item.price(), item.stock() > 0);
    }
}
