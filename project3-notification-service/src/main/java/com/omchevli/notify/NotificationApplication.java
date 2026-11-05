package com.omchevli.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Multi-Tenant Notification Service.
 *
 * Every notification belongs to exactly one tenant (header X-Tenant-Id on REST,
 * tenant field on WebSocket messages) and tenants are fully isolated: no tenant
 * can read or receive another tenant's notifications.
 *
 * Publishing goes through an {@link com.omchevli.notify.bus.EventBus}: an
 * in-memory bus by default, or Redis pub/sub with the "redis" profile
 * (channel per tenant: notifications:{tenantId}) so multiple instances can
 * fan out the same event. Live delivery to browsers is STOMP over WebSocket
 * on /topic/tenant-{tenantId}.
 */
@SpringBootApplication
public class NotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
}
