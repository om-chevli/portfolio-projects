package com.omchevli.notify.service;

import com.omchevli.notify.bus.EventBus;
import com.omchevli.notify.model.Notification;
import com.omchevli.notify.store.NotificationStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationService {

    public static final String DEFAULT_TENANT = "default";

    private final NotificationStore store;
    private final EventBus eventBus;
    private final com.omchevli.notify.websocket.LiveBroadcaster broadcaster;

    public NotificationService(NotificationStore store, EventBus eventBus,
                               com.omchevli.notify.websocket.LiveBroadcaster broadcaster) {
        this.store = store;
        this.eventBus = eventBus;
        this.broadcaster = broadcaster;
    }

    public static String normalizeTenant(String tenantId) {
        return (tenantId == null || tenantId.isBlank()) ? DEFAULT_TENANT : tenantId.trim();
    }

    /** Creates, stores and publishes a notification for exactly one tenant. */
    public Notification notify(String tenantId, String userId, String type, String message) {
        Notification notification = new Notification(normalizeTenant(tenantId), userId, type, message);
        store.save(notification);
        broadcaster.ensureSubscribed(notification.getTenantId());
        eventBus.publish(notification);
        return notification;
    }

    public List<Notification> listForUser(String tenantId, String userId) {
        return store.findByTenantAndUser(normalizeTenant(tenantId), userId);
    }

    public Notification markRead(String tenantId, String id) {
        Notification notification = store.findByTenantAndId(normalizeTenant(tenantId), id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.setRead(true);
        return notification;
    }
}
