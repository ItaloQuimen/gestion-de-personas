package com.imqh.personas.messaging;

import com.imqh.personas.service.PersonaService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PersonaCreationConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaCreationConsumer.class);

    private final PersonaService personaService;
    private final int maxAttempts;
    private final long initialInterval;
    private final double multiplier;
    private final long maxInterval;

    public PersonaCreationConsumer(
            PersonaService personaService,
            @Value("${app.rabbitmq.retry.max-attempts:3}") int maxAttempts,
            @Value("${app.rabbitmq.retry.initial-interval:1000}") long initialInterval,
            @Value("${app.rabbitmq.retry.multiplier:2.0}") double multiplier,
            @Value("${app.rabbitmq.retry.max-interval:5000}") long maxInterval) {
        this.personaService = personaService;
        this.maxAttempts = maxAttempts;
        this.initialInterval = initialInterval;
        this.multiplier = multiplier;
        this.maxInterval = maxInterval;
    }

    @RabbitListener(queues = RabbitMqConfig.PERSONA_CREATION_QUEUE, ackMode = "MANUAL")
    public void consumir(
            PersonaCreacionMensaje mensaje,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(name = AmqpHeaders.REDELIVERED, required = false) Boolean redelivered)
            throws IOException {
        long interval = initialInterval;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                personaService.procesarCreacion(mensaje);
                channel.basicAck(deliveryTag, false);
                return;
            } catch (RuntimeException exception) {
                if (attempt == maxAttempts) {
                    LOGGER.warn(
                            "No fue posible guardar la solicitud {} tras {} intentos; se reencolará. Redelivery: {}",
                            mensaje.solicitudId(),
                            maxAttempts,
                            Boolean.TRUE.equals(redelivered),
                            exception);
                    channel.basicNack(deliveryTag, false, true);
                    return;
                }
                esperarAntesDelSiguienteIntento(interval, channel, deliveryTag);
                interval = Math.min((long) (interval * multiplier), maxInterval);
            }
        }
    }

    private void esperarAntesDelSiguienteIntento(long interval, Channel channel, long deliveryTag)
            throws IOException {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            channel.basicNack(deliveryTag, false, true);
            throw new IllegalStateException("Se interrumpió el reintento de persistencia", exception);
        }
    }
}
