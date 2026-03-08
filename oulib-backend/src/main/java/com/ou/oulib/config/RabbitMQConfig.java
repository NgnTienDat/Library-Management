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


    @Bean
    public TopicExchange postExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean
    public Queue postCreatedQueue() {
        return QueueBuilder.durable(NOTIFICATION_CREATED_QUEUE).build();
    }

    @Bean
    public Binding postCreatedBinding() {
        return BindingBuilder
                .bind(postCreatedQueue())
                .to(postExchange())
                .with(NOTIFICATION_CREATED_ROUTING_KEY);
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
