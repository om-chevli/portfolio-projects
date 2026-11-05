package com.omchevli.notify.bus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omchevli.notify.model.Notification;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Redis pub/sub bus, active with the "redis" profile. Each tenant gets its own
 * channel (notifications:{tenantId}), so fan-out across service instances keeps
 * the same per-tenant isolation as the in-memory bus. Payloads are JSON.
 * Not loaded by default: the app and tests run with no Redis server.
 */
@Component
@Profile("redis")
public class RedisEventBus implements EventBus {

    static final String CHANNEL_PREFIX = "notifications:";

    private final StringRedisTemplate redis;
    private final RedisMessageListenerContainer container;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    public RedisEventBus(StringRedisTemplate redis, RedisMessageListenerContainer container) {
        this.redis = redis;
        this.container = container;
    }

    @Override
    public void publish(Notification notification) {
        try {
            redis.convertAndSend(CHANNEL_PREFIX + notification.getTenantId(),
                    objectMapper.writeValueAsString(notification));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize notification", e);
        }
    }

    @Override
    public void subscribe(String tenantId, Consumer<Notification> listener) {
        container.addMessageListener((message, pattern) -> {
            try {
                listener.accept(objectMapper.readValue(message.getBody(), Notification.class));
            } catch (Exception e) {
                throw new IllegalStateException("Could not deserialize notification", e);
            }
        }, new ChannelTopic(CHANNEL_PREFIX + tenantId));
    }
}
