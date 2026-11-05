package com.omchevli.notify;

import com.omchevli.notify.bus.InMemoryEventBus;
import com.omchevli.notify.model.Notification;
import com.omchevli.notify.service.NotificationService;
import com.omchevli.notify.store.NotificationStore;
import com.omchevli.notify.websocket.LiveBroadcaster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificationServiceTest {

    private NotificationService service;
    private InMemoryEventBus bus;
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        bus = new InMemoryEventBus();
        messagingTemplate = mock(SimpMessagingTemplate.class);
        LiveBroadcaster broadcaster = new LiveBroadcaster(bus, messagingTemplate);
        service = new NotificationService(new NotificationStore(), bus, broadcaster);
    }

    @Test
    void tenantsAreIsolated() {
        service.notify("acme", "u1", "TASK", "Acme task assigned");
        service.notify("globex", "u1", "TASK", "Globex task assigned");

        List<Notification> acme = service.listForUser("acme", "u1");
        assertEquals(1, acme.size());
        assertEquals("Acme task assigned", acme.get(0).getMessage());
        assertEquals(1, service.listForUser("globex", "u1").size());
    }

    @Test
    void missingTenantFallsBackToDefault() {
        service.notify(null, "u1", "INFO", "hello");
        assertEquals(1, service.listForUser(null, "u1").size());
        assertEquals(1, service.listForUser("default", "u1").size());
    }

    @Test
    void publishBroadcastsOnlyToOwningTenantTopic() {
        service.notify("acme", "u1", "TASK", "ping");
        verify(messagingTemplate).convertAndSend(eq("/topic/tenant-acme"), any(Notification.class));
    }

    @Test
    void busListenersOnlyReceiveTheirTenant() {
        List<Notification> received = new java.util.ArrayList<>();
        bus.subscribe("acme", received::add);
        service.notify("globex", "u1", "TASK", "not for acme");
        assertTrue(received.isEmpty());
        service.notify("acme", "u1", "TASK", "for acme");
        assertEquals(1, received.size());
    }

    @Test
    void markReadRespectsTenant() {
        Notification n = service.notify("acme", "u1", "TASK", "read me");
        assertThrows(ResponseStatusException.class, () -> service.markRead("globex", n.getId()));
        assertTrue(service.markRead("acme", n.getId()).isRead());
    }
}
