package com.omchevli.notify.websocket;

import com.omchevli.notify.bus.EventBus;
import com.omchevli.notify.model.Notification;
import jakarta.annotation.PostConstruct;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Bridges the event bus to WebSocket topics. Subscribes per tenant the first
 * time a notification for that tenant is published (the service calls
 * {@link #ensureSubscribed(String)}), then forwards bus events to
 * /topic/tenant-{tenantId} so subscribed browsers update in real time.
 *
 * Note: with the Redis bus, subscription is per-instance; with the in-memory
 * bus this is a direct in-process bridge. Either way the tenant topic is the
 * isolation boundary on the client side.
 */
@Component
public class LiveBroadcaster {

    private final EventBus eventBus;
    private final SimpMessagingTemplate messagingTemplate;
    private final java.util.Set<String> subscribedTenants = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public LiveBroadcaster(EventBus eventBus, SimpMessagingTemplate messagingTemplate) {
        this.eventBus = eventBus;
        this.messagingTemplate = messagingTemplate;
    }

    public void ensureSubscribed(String tenantId) {
        if (subscribedTenants.add(tenantId)) {
            eventBus.subscribe(tenantId, this::broadcast);
        }
    }

    public void broadcast(Notification notification) {
        messagingTemplate.convertAndSend("/topic/tenant-" + notification.getTenantId(), notification);
    }

    @PostConstruct
    void subscribeDefaultTenant() {
        ensureSubscribed(com.omchevli.notify.service.NotificationService.DEFAULT_TENANT);
    }
}
