package com.ou.oulib.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_CREATED_QUEUE = "notification.created.queue";
    public static final String NOTIFICATION_CREATED_ROUTING_KEY = "notification.created";

    // Borrow reminder configuration
    public static final String BORROW_EXCHANGE = "borrow.exchange";
    public static final String BORROW_REMINDER_QUEUE = "borrow-reminder-queue";
    public static final String BORROW_REMINDER_ROUTING_KEY = "borrow.reminder";

    // Audit log configuration
    public static final String AUDIT_EXCHANGE = "audit.exchange";
    public static final String AUDIT_QUEUE = "audit.queue";
    public static final String AUDIT_ROUTING_KEY = "audit.log";


    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationCreatedQueue() {
        return QueueBuilder.durable(NOTIFICATION_CREATED_QUEUE).build();
    }

    @Bean
    public Binding notificationCreatedBinding() {
        return BindingBuilder
                .bind(notificationCreatedQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_CREATED_ROUTING_KEY);
    }

    // Borrow reminder exchange
//    @Bean
//    public TopicExchange borrowExchange() {
//        return new TopicExchange(BORROW_EXCHANGE, true, false);
//    }

    @Bean
    public DirectExchange borrowDirectExchange() {
        return new DirectExchange(BORROW_EXCHANGE, true, false);
    }
    // Borrow reminder queue
    @Bean
    public Queue borrowReminderQueue() {
        return QueueBuilder.durable(BORROW_REMINDER_QUEUE).build();
    }

    // Bind borrow reminder queue to exchange
    @Bean
    public Binding borrowReminderBinding() {
        return BindingBuilder
                .bind(borrowReminderQueue())
                .to(borrowDirectExchange())
                .with(BORROW_REMINDER_ROUTING_KEY);
    }

    @Bean
    public DirectExchange auditDirectExchange() {
        return new DirectExchange(AUDIT_EXCHANGE, true, false);
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(AUDIT_QUEUE).build();
    }

    @Bean
    public Binding auditBinding() {
        return BindingBuilder
                .bind(auditQueue())
                .to(auditDirectExchange())
                .with(AUDIT_ROUTING_KEY);
    }


    @Bean
    public JacksonJsonMessageConverter jacksonConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter jacksonMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        return template;
    }
}
