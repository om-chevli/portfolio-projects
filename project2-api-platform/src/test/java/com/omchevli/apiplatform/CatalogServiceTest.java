package com.omchevli.apiplatform;

import com.omchevli.apiplatform.model.CatalogItem;
import com.omchevli.apiplatform.repository.CatalogRepository;
import com.omchevli.apiplatform.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    CatalogRepository repository;

    @InjectMocks
    CatalogService service;

    private final CatalogItem keyboard = new CatalogItem("kb-001", "Keyboard", "peripherals",
            new BigDecimal("129.99"), new BigDecimal("74.50"), 42);
    private final CatalogItem monitor = new CatalogItem("mn-002", "Monitor", "displays",
            new BigDecimal("329.00"), new BigDecimal("210.00"), 0);

    @Test
    void listFiltersByCategoryCaseInsensitively() {
        when(repository.findAll()).thenReturn(List.of(keyboard, monitor));
        assertEquals(List.of(monitor), service.list("DISPLAYS"));
        assertEquals(2, service.list(null).size());
    }

    @Test
    void getMissingThrows404() {
        when(repository.findById("nope")).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.get("nope"));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void createRejectsNegativePrice() {
        CatalogItem bad = new CatalogItem("x", "Bad", "misc",
                new BigDecimal("-1"), BigDecimal.ZERO, 1);
        assertThrows(ResponseStatusException.class, () -> service.create(bad));
    }

    @Test
    void createDelegatesToRepository() {
        when(repository.save(keyboard)).thenReturn(keyboard);
        assertEquals(keyboard, service.create(keyboard));
        verify(repository).save(keyboard);
    }
}
