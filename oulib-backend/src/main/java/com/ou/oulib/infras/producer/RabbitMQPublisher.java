package com.ou.oulib.infras.producer;

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

//    public void publishActionMessage(ActionMessage message) {
//        rabbitTemplate.convertAndSend(
//                RabbitMQConfig.ACTION_EXCHANGE,
//                RabbitMQConfig.ACTION_ROUTING_KEY,
//                message
//        );
//    }
}

