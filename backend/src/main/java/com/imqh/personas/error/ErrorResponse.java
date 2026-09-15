package com.imqh.personas.error;

import java.time.LocalDateTime;

public record ErrorResponse(
        int estado,
        String mensaje,
        LocalDateTime fechaHora) {

    public static ErrorResponse of(int estado, String mensaje) {
        return new ErrorResponse(estado, mensaje, LocalDateTime.now());
    }
}
