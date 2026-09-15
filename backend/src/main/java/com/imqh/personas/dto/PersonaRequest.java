package com.imqh.personas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PersonaRequest(
        @NotBlank String rut,
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotNull @Past LocalDate fechaNacimiento,
        @NotNull @Valid DireccionRequest direccion) {

    public record DireccionRequest(
            @NotBlank String calle,
            @NotBlank String comuna,
            @NotBlank String region) {
    }
}
