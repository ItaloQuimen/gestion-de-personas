package com.imqh.personas.messaging;

import com.imqh.personas.dto.PersonaRequest;

import java.time.LocalDate;
import java.util.UUID;

public record PersonaCreacionMensaje(
        String solicitudId,
        String rut,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        DireccionMensaje direccion) {

    public static PersonaCreacionMensaje from(PersonaRequest request) {
        return new PersonaCreacionMensaje(
                UUID.randomUUID().toString(),
                request.rut(),
                request.nombre(),
                request.apellido(),
                request.fechaNacimiento(),
                new DireccionMensaje(
                        request.direccion().calle(),
                        request.direccion().comuna(),
                        request.direccion().region()));
    }

    public record DireccionMensaje(
            String calle,
            String comuna,
            String region) {
    }
}
