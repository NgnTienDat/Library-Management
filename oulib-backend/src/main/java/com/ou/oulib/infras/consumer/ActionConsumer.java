package com.ou.oulib.infras.consumer;

import com.ou.oulib.config.RabbitMQConfig;
import com.ou.oulib.infras.event.RemindNotification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//@ConditionalOnProperty(name = "app.worker.enabled", havingValue = "true")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActionConsumer {



    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_CREATED_QUEUE)
    public void handle(RemindNotification message) {
//        try {
//            actionWorker.handle(message);
//        } catch (Exception e) {
//            log.error("Failed to process message {}", message, e);
//            throw e; // để Rabbit retry
//        }
        System.out.println("Message received in ActionConsumer: " + message);
    }
}
