package com.imqh.personas.service;

import com.imqh.personas.domain.Persona;
import com.imqh.personas.domain.Direccion;
import com.imqh.personas.dto.PersonaRequest;
import com.imqh.personas.dto.PersonaResponse;
import com.imqh.personas.repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
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

    public PersonaResponse crear(PersonaRequest request) {
        return PersonaResponse.from(personaRepository.save(toPersona(request)));
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

    private NoSuchElementException personaNoEncontrada(Long id) {
        return new NoSuchElementException("Persona no encontrada: " + id);
    }

    private Persona toPersona(PersonaRequest request) {
        return new Persona(
                request.rut(),
                request.nombre(),
                request.apellido(),
                request.fechaNacimiento(),
                toDireccion(request.direccion()));
    }

    private Direccion toDireccion(PersonaRequest.DireccionRequest request) {
        return new Direccion(request.calle(), request.comuna(), request.region());
    }
}
