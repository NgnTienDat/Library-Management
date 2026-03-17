package com.ou.oulib.infras.producer;

import com.ou.oulib.config.RabbitMQConfig;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BorrowReminderProducer {

    RabbitTemplate rabbitTemplate;

    public void publishBorrowReminder(String borrowRecordId) {
//        log.info("Publishing borrow reminder for record: {}", borrowRecordId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BORROW_EXCHANGE,
                RabbitMQConfig.BORROW_REMINDER_ROUTING_KEY,
                borrowRecordId
        );
    }
}
