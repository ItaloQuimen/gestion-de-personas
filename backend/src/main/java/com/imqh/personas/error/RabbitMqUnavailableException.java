package com.imqh.personas.error;

public class RabbitMqUnavailableException extends RuntimeException {

    public RabbitMqUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public RabbitMqUnavailableException(String message) {
        super(message);
    }
}
