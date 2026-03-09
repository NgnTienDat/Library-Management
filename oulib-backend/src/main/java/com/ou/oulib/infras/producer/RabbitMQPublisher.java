package com.ou.oulib.infras.producer;

import com.ou.oulib.config.RabbitMQConfig;
import com.ou.oulib.infras.event.ActionMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMQPublisher {

    RabbitTemplate rabbitTemplate;

    public void publishActionMessage(ActionMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_CREATED_ROUTING_KEY,
                message
        );
    }
}

