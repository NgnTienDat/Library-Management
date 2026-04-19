package com.ou.oulib.infras.producer;

import com.ou.oulib.config.RabbitMQConfig;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailReminderPublisher {

    RabbitTemplate rabbitTemplate;

    public void publishBorrowEmailReminder(String borrowRecordId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BORROW_EXCHANGE,
                RabbitMQConfig.BORROW_REMINDER_ROUTING_KEY,
                borrowRecordId,
                message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                }
        );
    }
}
