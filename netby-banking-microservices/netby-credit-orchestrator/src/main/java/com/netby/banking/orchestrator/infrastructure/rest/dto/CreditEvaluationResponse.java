package com.netby.banking.orchestrator.infrastructure.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CreditEvaluationResponse {

    public Long id;
    public String cedula;
    public Double montoSolicitado;
    public Integer plazoAnios;
    public Double salario;
    public Integer scoreObtenido;
    public Double deudaMensual;
    public String estado;
    public LocalDateTime fechaEvaluacion;
    public String motivoRechazo;
    public List<DebtDetail> deudas;

    public static class DebtDetail {
        public String descripcion;
        public Double mensualidad;

        public DebtDetail(String descripcion, Double mensualidad) {
            this.descripcion = descripcion;
            this.mensualidad = mensualidad;
        }
    }
}
