package com.imqh.personas.service;

import com.imqh.personas.messaging.PersonaCreacionMensaje;
import com.imqh.personas.messaging.PersonaCreationPublisher;
import com.imqh.personas.repository.PersonaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonaServiceTests {

    @Test
    void noDuplicaUnaSolicitudYaPersistida() {
        PersonaRepository personaRepository = mock(PersonaRepository.class);
        PersonaCreationPublisher publisher = mock(PersonaCreationPublisher.class);
        PersonaService personaService = new PersonaService(personaRepository, publisher);
        PersonaCreacionMensaje mensaje = new PersonaCreacionMensaje(
                "8f691b2b-8eab-431d-9729-728c539d9328",
                "12.345.678-5",
                "Ana",
                "Pérez",
                LocalDate.of(1990, 1, 1),
                new PersonaCreacionMensaje.DireccionMensaje("Uno 123", "Santiago", "Metropolitana"));
        when(personaRepository.existsBySolicitudId(mensaje.solicitudId())).thenReturn(true);

        personaService.procesarCreacion(mensaje);

        verify(personaRepository).existsBySolicitudId(mensaje.solicitudId());
        verify(personaRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
