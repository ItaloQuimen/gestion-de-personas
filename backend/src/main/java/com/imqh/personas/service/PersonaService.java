package com.imqh.personas.service;

import com.imqh.personas.domain.Persona;
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

    public List<Persona> listar() {
        return personaRepository.findAll();
    }

    public Persona buscarPorId(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> personaNoEncontrada(id));
    }

    public Persona crear(Persona persona) {
        return personaRepository.save(persona);
    }

    public Persona actualizar(Long id, Persona datosActualizados) {
        Persona persona = buscarPorId(id);
        persona.setRut(datosActualizados.getRut());
        persona.setNombre(datosActualizados.getNombre());
        persona.setApellido(datosActualizados.getApellido());
        persona.setFechaNacimiento(datosActualizados.getFechaNacimiento());
        persona.setDireccion(datosActualizados.getDireccion());
        return personaRepository.save(persona);
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
}
