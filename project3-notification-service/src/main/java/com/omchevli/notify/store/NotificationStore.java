package com.omchevli.notify.store;

import com.omchevli.notify.model.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Per-tenant in-memory store. Keying every structure by tenantId is what enforces isolation. */
@Component
public class NotificationStore {

    private final Map<String, List<Notification>> byTenant = new ConcurrentHashMap<>();

    public Notification save(Notification notification) {
        byTenant.computeIfAbsent(notification.getTenantId(), t -> new CopyOnWriteArrayList<>())
                .add(notification);
        return notification;
    }

    public List<Notification> findByTenantAndUser(String tenantId, String userId) {
        return byTenant.getOrDefault(tenantId, List.of()).stream()
                .filter(n -> n.getUserId().equals(userId))
                .toList();
    }

    public Optional<Notification> findByTenantAndId(String tenantId, String id) {
        return byTenant.getOrDefault(tenantId, List.of()).stream()
                .filter(n -> n.getId().equals(id))
                .findFirst();
    }
}
