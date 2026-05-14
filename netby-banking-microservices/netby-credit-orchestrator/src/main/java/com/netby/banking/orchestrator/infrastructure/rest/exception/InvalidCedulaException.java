package com.netby.banking.orchestrator.infrastructure.rest.exception;

public class InvalidCedulaException extends RuntimeException {

    public InvalidCedulaException(String cedula) {
        super("Cédula inválida: '" + cedula +
                "' no cumple el algoritmo de Módulo 10 para cédulas ecuatorianas");
    }
}
