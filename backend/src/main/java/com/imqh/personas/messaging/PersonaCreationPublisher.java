package com.imqh.personas.messaging;

import com.imqh.personas.error.RabbitMqUnavailableException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class PersonaCreationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final long confirmTimeout;

    public PersonaCreationPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.publisher-confirm-timeout:5000}") long confirmTimeout) {
        this.rabbitTemplate = rabbitTemplate;
        this.confirmTimeout = confirmTimeout;
    }

    public void publicar(PersonaCreacionMensaje mensaje) {
        CorrelationData correlationData = new CorrelationData(mensaje.solicitudId());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.PERSONA_EXCHANGE,
                    RabbitMqConfig.PERSONA_CREATION_ROUTING_KEY,
                    mensaje,
                    message -> {
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        message.getMessageProperties().setMessageId(mensaje.solicitudId());
                        return message;
                    },
                    correlationData);

            CorrelationData.Confirm confirm = correlationData.getFuture()
                    .get(confirmTimeout, TimeUnit.MILLISECONDS);
            if (!confirm.ack() || correlationData.getReturned() != null) {
                throw new RabbitMqUnavailableException("RabbitMQ no confirmó la solicitud de creación");
            }
        } catch (AmqpException | InterruptedException | ExecutionException | TimeoutException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RabbitMqUnavailableException("RabbitMQ no está disponible para recibir la solicitud", exception);
        }
    }
}
