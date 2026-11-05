package com.omchevli.notify.bus;

import com.omchevli.notify.model.Notification;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** Default bus for single-instance runs and tests: synchronous, per-tenant listeners. */
@Component
@Primary
@Profile("!redis")
public class InMemoryEventBus implements EventBus {

    private final Map<String, List<Consumer<Notification>>> listeners = new ConcurrentHashMap<>();

    @Override
    public void publish(Notification notification) {
        listeners.getOrDefault(notification.getTenantId(), List.of())
                .forEach(l -> l.accept(notification));
    }

    @Override
    public void subscribe(String tenantId, Consumer<Notification> listener) {
        listeners.computeIfAbsent(tenantId, t -> new CopyOnWriteArrayList<>()).add(listener);
    }
}
