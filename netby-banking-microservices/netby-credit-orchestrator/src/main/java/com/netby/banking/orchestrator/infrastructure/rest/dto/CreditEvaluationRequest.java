package com.netby.banking.orchestrator.infrastructure.rest.dto;

import jakarta.validation.constraints.*;

public class CreditEvaluationRequest {

    @NotBlank(message = "La cédula es requerida")
    @Pattern(regexp = "^[0-9]{10}$", message = "La cédula debe tener exactamente 10 dígitos numéricos")
    public String cedula;

    @NotNull(message = "El monto solicitado es requerido")
    @Positive(message = "El monto solicitado debe ser positivo")
    public Double montoSolicitado;

    @NotNull(message = "El plazo en años es requerido")
    @Positive(message = "El plazo debe ser positivo")
    @Max(value = 30, message = "El plazo máximo es 30 años")
    public Integer plazoAnios;

    @NotNull(message = "El salario es requerido")
    @Positive(message = "El salario debe ser positivo")
    public Double salario;
}
