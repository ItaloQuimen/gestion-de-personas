package com.imqh.personas.service;

import com.imqh.personas.domain.Direccion;
import com.imqh.personas.domain.Persona;
import com.imqh.personas.dto.PersonaRequest;
import com.imqh.personas.dto.PersonaResponse;
import com.imqh.personas.dto.SolicitudCreacionResponse;
import com.imqh.personas.messaging.PersonaCreationPublisher;
import com.imqh.personas.messaging.PersonaCreacionMensaje;
import com.imqh.personas.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final PersonaCreationPublisher personaCreationPublisher;

    public PersonaService(
            PersonaRepository personaRepository,
            PersonaCreationPublisher personaCreationPublisher) {
        this.personaRepository = personaRepository;
        this.personaCreationPublisher = personaCreationPublisher;
    }

    public List<PersonaResponse> listar() {
        return personaRepository.findAll().stream()
                .map(PersonaResponse::from)
                .toList();
    }

    public PersonaResponse buscarPorId(Long id) {
        return PersonaResponse.from(personaRepository.findById(id)
                .orElseThrow(() -> personaNoEncontrada(id)));
    }

    public SolicitudCreacionResponse crear(PersonaRequest request) {
        PersonaCreacionMensaje mensaje = PersonaCreacionMensaje.from(request);
        personaCreationPublisher.publicar(mensaje);
        return new SolicitudCreacionResponse(mensaje.solicitudId(), "PENDIENTE");
    }

    public PersonaResponse actualizar(Long id, PersonaRequest request) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> personaNoEncontrada(id));
        persona.setRut(request.rut());
        persona.setNombre(request.nombre());
        persona.setApellido(request.apellido());
        persona.setFechaNacimiento(request.fechaNacimiento());
        persona.setDireccion(toDireccion(request.direccion()));
        return PersonaResponse.from(personaRepository.save(persona));
    }

    public void eliminar(Long id) {
        if (!personaRepository.existsById(id)) {
            throw personaNoEncontrada(id);
        }
        personaRepository.deleteById(id);
    }

    @Transactional
    public void procesarCreacion(PersonaCreacionMensaje mensaje) {
        if (personaRepository.existsBySolicitudId(mensaje.solicitudId())) {
            return;
        }
        personaRepository.save(toPersona(mensaje));
    }

    private NoSuchElementException personaNoEncontrada(Long id) {
        return new NoSuchElementException("Persona no encontrada: " + id);
    }

    private Persona toPersona(PersonaCreacionMensaje mensaje) {
        return new Persona(
                mensaje.solicitudId(),
                mensaje.rut(),
                mensaje.nombre(),
                mensaje.apellido(),
                mensaje.fechaNacimiento(),
                new Direccion(
                        mensaje.direccion().calle(),
                        mensaje.direccion().comuna(),
                        mensaje.direccion().region()));
    }

    private Direccion toDireccion(PersonaRequest.DireccionRequest request) {
        return new Direccion(request.calle(), request.comuna(), request.region());
    }
}
