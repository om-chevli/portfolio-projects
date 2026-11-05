package com.omchevli.notify.controller;

import com.omchevli.notify.model.Notification;
import com.omchevli.notify.service.NotificationService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API. The tenant is taken from the X-Tenant-Id header on every call —
 * there is deliberately no way to ask for another tenant's data, because the
 * tenant is part of the caller's identity, not a query parameter.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    public record CreateRequest(@NotBlank String userId, @NotBlank String type, @NotBlank String message) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notification create(@RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
                               @RequestBody CreateRequest req) {
        return service.notify(tenantId, req.userId(), req.type(), req.message());
    }

    @GetMapping
    public List<Notification> list(@RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
                                   @RequestParam String userId) {
        return service.listForUser(tenantId, userId);
    }

    @PostMapping("/{id}/read")
    public Notification markRead(@RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
                                 @PathVariable String id) {
        return service.markRead(tenantId, id);
    }
}
