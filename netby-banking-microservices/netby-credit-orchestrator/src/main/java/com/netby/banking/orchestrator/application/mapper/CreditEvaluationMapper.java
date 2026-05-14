package com.netby.banking.orchestrator.application.mapper;

import com.netby.banking.orchestrator.domain.model.CreditEvaluation;
import com.netby.banking.orchestrator.infrastructure.rest.dto.CreditEvaluationResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreditEvaluationMapper {

    public CreditEvaluationResponse toResponse(CreditEvaluation e) {
        CreditEvaluationResponse r = new CreditEvaluationResponse();
        r.id = e.id;
        r.cedula = e.cedula;
        r.montoSolicitado = e.montoSolicitado;
        r.plazoAnios = e.plazoAnios;
        r.salario = e.salario;
        r.scoreObtenido = e.scoreObtenido;
        r.deudaMensual = e.deudaMensual;
        r.estado = e.estado.name();
        r.fechaEvaluacion = e.fechaEvaluacion;
        r.motivoRechazo = e.motivoRechazo;
        return r;
    }
}
