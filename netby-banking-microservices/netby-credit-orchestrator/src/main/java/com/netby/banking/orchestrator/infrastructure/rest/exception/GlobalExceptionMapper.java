package com.netby.banking.orchestrator.infrastructure.rest.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof InvalidCedulaException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, exception.getMessage()))
                    .build();
        }

        if (exception instanceof CreditEvaluationNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody(404, exception.getMessage()))
                    .build();
        }

        if (exception instanceof ConstraintViolationException cve) {
            String details = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "status", 400,
                            "error", "Validación fallida",
                            "detalles", details,
                            "timestamp", LocalDateTime.now().toString()))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorBody(500, "Error interno del servidor: " + exception.getMessage()))
                .build();
    }

    private Map<String, Object> errorBody(int status, String message) {
        return Map.of(
                "status", status,
                "error", message,
                "timestamp", LocalDateTime.now().toString());
    }
}
