package com.omchevli.notify.controller;

import com.omchevli.notify.model.Notification;
import com.omchevli.notify.service.NotificationService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * WebSocket entry point for workflow events: a client (or another service
 * bridging to STOMP) sends an event to /app/workflow-events, and every
 * subscriber of that tenant's topic sees the resulting notification live.
 * This is the "workflow automation" trigger surface of the service.
 */
@Controller
public class WorkflowController {

    private final NotificationService service;

    public WorkflowController(NotificationService service) {
        this.service = service;
    }

    public record WorkflowEvent(String tenantId, @NotBlank String userId,
                                @NotBlank String type, @NotBlank String message) {}

    @MessageMapping("/workflow-events")
    public void onWorkflowEvent(WorkflowEvent event) {
        Notification n = service.notify(event.tenantId(), event.userId(), event.type(), event.message());
        // notify() already published to the bus; the LiveBroadcaster forwards it to the tenant topic.
        if (n == null) {
            throw new IllegalStateException("unreachable");
        }
    }
}
