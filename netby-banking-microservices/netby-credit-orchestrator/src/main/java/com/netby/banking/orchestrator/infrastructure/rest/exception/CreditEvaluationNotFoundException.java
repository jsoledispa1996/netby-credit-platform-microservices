package com.netby.banking.orchestrator.infrastructure.rest.exception;

public class CreditEvaluationNotFoundException extends RuntimeException {

    public static CreditEvaluationNotFoundException porId(Long id) {
        return new CreditEvaluationNotFoundException(
                "Evaluación crediticia no encontrada con ID: " + id);
    }

    public static CreditEvaluationNotFoundException porCedula(String cedula) {
        return new CreditEvaluationNotFoundException(
                "No se encontraron evaluaciones para la cédula: " + cedula);
    }

    public CreditEvaluationNotFoundException(String mensaje) {
        super(mensaje);
    }
}
