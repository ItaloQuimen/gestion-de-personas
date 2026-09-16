package com.imqh.personas.messaging;

import com.imqh.personas.service.PersonaService;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PersonaCreationConsumerTests {

    private static final long DELIVERY_TAG = 7L;

    @Test
    void confirmaElMensajeDespuesDeGuardarLaPersona() throws Exception {
        PersonaService personaService = mock(PersonaService.class);
        Channel channel = mock(Channel.class);
        PersonaCreationConsumer consumer = new PersonaCreationConsumer(personaService, 3, 0, 1, 0);
        PersonaCreacionMensaje mensaje = mensaje();

        consumer.consumir(mensaje, channel, DELIVERY_TAG, false);

        verify(personaService).procesarCreacion(mensaje);
        verify(channel).basicAck(DELIVERY_TAG, false);
        verify(channel, never()).basicNack(DELIVERY_TAG, false, true);
    }

    @Test
    void reencolaElMensajeCuandoSeAgotanLosIntentos() throws Exception {
        PersonaService personaService = mock(PersonaService.class);
        Channel channel = mock(Channel.class);
        PersonaCreationConsumer consumer = new PersonaCreationConsumer(personaService, 3, 0, 1, 0);
        PersonaCreacionMensaje mensaje = mensaje();
        doThrow(new IllegalStateException("MySQL no disponible"))
                .when(personaService).procesarCreacion(mensaje);

        consumer.consumir(mensaje, channel, DELIVERY_TAG, true);

        verify(personaService, times(3)).procesarCreacion(mensaje);
        verify(channel, never()).basicAck(DELIVERY_TAG, false);
        verify(channel).basicNack(DELIVERY_TAG, false, true);
    }

    private PersonaCreacionMensaje mensaje() {
        return new PersonaCreacionMensaje(
                "8f691b2b-8eab-431d-9729-728c539d9328",
                "12.345.678-5",
                "Ana",
                "Pérez",
                LocalDate.of(1990, 1, 1),
                new PersonaCreacionMensaje.DireccionMensaje("Uno 123", "Santiago", "Metropolitana"));
    }
}
