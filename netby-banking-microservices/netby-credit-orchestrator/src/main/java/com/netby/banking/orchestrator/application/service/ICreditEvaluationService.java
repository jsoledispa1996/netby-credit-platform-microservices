package com.netby.banking.orchestrator.application.service;

import com.netby.banking.orchestrator.infrastructure.rest.dto.CreditEvaluationRequest;
import com.netby.banking.orchestrator.infrastructure.rest.dto.CreditEvaluationResponse;

import java.util.List;

/**
 * Contrato del servicio de evaluación crediticia.
 * Sigue el principio de inversión de dependencias (SOLID - D).
 */
public interface ICreditEvaluationService {

    /**
     * Evalúa una solicitud de crédito consultando el servicio de riesgos
     * y aplica las reglas de negocio para aprobar o rechazar.
     *
     * @param request datos de la solicitud (cédula, monto, plazo, salario)
     * @return resultado de la evaluación con score, deudas y decisión
     */
    CreditEvaluationResponse evaluate(CreditEvaluationRequest request);

    /**
     * Retorna el historial completo de evaluaciones almacenadas.
     *
     * @return lista de evaluaciones persistidas
     */
    List<CreditEvaluationResponse> findAll();
}
