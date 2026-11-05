package com.omchevli.notify.bus;

import com.omchevli.notify.model.Notification;

import java.util.function.Consumer;

/**
 * Pub/sub seam between "a notification was created" and "deliver it live".
 * Implementations: in-memory (default) and Redis pub/sub (profile "redis").
 */
public interface EventBus {
    void publish(Notification notification);
    void subscribe(String tenantId, Consumer<Notification> listener);
}
