package com.ou.oulib.infras.consumer;

import com.ou.oulib.config.RabbitMQConfig;
import com.ou.oulib.entity.AuditLog;
import com.ou.oulib.enums.AuditAction;
import com.ou.oulib.enums.ResourceType;
import com.ou.oulib.infras.event.AuditMessage;
import com.ou.oulib.repository.AuditLogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@ConditionalOnProperty(name = "app.worker.enabled", havingValue = "true")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuditLogConsumer {

    AuditLogRepository auditLogRepository;

    @RabbitListener(queues = RabbitMQConfig.AUDIT_QUEUE)
    public void consumeAuditLog(AuditMessage message) {
        if (message == null) {
            log.warn("Received null audit message");
            return;
        }

        try {
            AuditAction action = AuditAction.valueOf(message.getAction());
            ResourceType resourceType = ResourceType.valueOf(message.getResourceType());

            AuditLog auditLog = AuditLog.builder()
                    .userId(message.getUserId() != null ? message.getUserId() : 0L)
                    .action(action)
                    .resourceType(resourceType)
                    .resourceId(message.getResourceId())
                    .oldValue(message.getOldValue())
                    .newValue(message.getNewValue())
                    .createdAt(message.getTimestamp() != null ? message.getTimestamp() : Instant.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Saved audit log: action={}, resourceType={}", action, resourceType);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid audit enum value in message: {}", message, ex);
        } catch (Exception ex) {
            log.error("Failed to persist audit log for message: {}", message, ex);
        }
    }
}
