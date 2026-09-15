package com.imqh.personas.dto;

import com.imqh.personas.domain.Direccion;
import com.imqh.personas.domain.Persona;

import java.time.LocalDate;

public record PersonaResponse(
        Long id,
        String rut,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        DireccionResponse direccion) {

    public static PersonaResponse from(Persona persona) {
        Direccion direccion = persona.getDireccion();
        DireccionResponse direccionResponse = direccion == null
                ? null
                : new DireccionResponse(
                        direccion.getCalle(),
                        direccion.getComuna(),
                        direccion.getRegion());

        return new PersonaResponse(
                persona.getId(),
                persona.getRut(),
                persona.getNombre(),
                persona.getApellido(),
                persona.getFechaNacimiento(),
                direccionResponse);
    }

    public record DireccionResponse(
            String calle,
            String comuna,
            String region) {
    }
}
