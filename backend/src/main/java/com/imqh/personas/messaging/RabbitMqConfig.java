package com.imqh.personas.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String PERSONA_EXCHANGE = "personas.exchange";
    public static final String PERSONA_CREATION_QUEUE = "personas.creacion";
    public static final String PERSONA_CREATION_ROUTING_KEY = "personas.crear";

    @Bean
    DirectExchange personaExchange() {
        return new DirectExchange(PERSONA_EXCHANGE, true, false);
    }

    @Bean
    Queue personaCreationQueue() {
        return QueueBuilder.durable(PERSONA_CREATION_QUEUE).build();
    }

    @Bean
    Binding personaCreationBinding(Queue personaCreationQueue, DirectExchange personaExchange) {
        return BindingBuilder.bind(personaCreationQueue)
                .to(personaExchange)
                .with(PERSONA_CREATION_ROUTING_KEY);
    }

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter("com.imqh.personas.messaging");
    }

}
