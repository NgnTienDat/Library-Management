package com.ou.oulib.infras.producer;

import com.ou.oulib.config.RabbitMQConfig;
import com.ou.oulib.infras.event.AuditMessage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuditProducer {

    RabbitTemplate rabbitTemplate;

    public void sendAuditLog(AuditMessage message) {
        if (message == null) {
            return;
        }

        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish(message);
                }
            });
            return;
        }

        publish(message);
    }

    private void publish(AuditMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.AUDIT_EXCHANGE,
                    RabbitMQConfig.AUDIT_ROUTING_KEY,
                    message
            );
        } catch (Exception ex) {
            log.error("Failed to publish audit log message: {}", message, ex);
        }
    }
}
