package com.netby.banking.orchestrator.application.service;

import com.netby.banking.orchestrator.application.mapper.CreditEvaluationMapper;
import com.netby.banking.orchestrator.domain.model.CreditEvaluation;
import com.netby.banking.orchestrator.domain.model.CreditEvaluation.EvaluationStatus;
import com.netby.banking.orchestrator.domain.validation.CedulaValidator;
import com.netby.banking.orchestrator.infrastructure.rest.dto.CreditEvaluationRequest;
import com.netby.banking.orchestrator.infrastructure.rest.dto.CreditEvaluationResponse;
import com.netby.banking.orchestrator.infrastructure.rest.exception.InvalidCedulaException;
import com.netby.banking.risk.grpc.*;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@ApplicationScoped
public class CreditEvaluationService implements ICreditEvaluationService {

    @GrpcClient("risk-service")
    RiskService riskServiceClient;

    @Inject
    CedulaValidator cedulaValidator;

    @Inject
    CreditEvaluationMapper mapper;

    @Override
    @Transactional
    public CreditEvaluationResponse evaluate(CreditEvaluationRequest request) {
        if (!cedulaValidator.isValid(request.cedula)) {
            throw new InvalidCedulaException(request.cedula);
        }

        // Llamadas paralelas al servicio de riesgos via gRPC (HTTP/2 multiplexing)
        AtomicReference<ScoreResponse> scoreRef = new AtomicReference<>();
        AtomicReference<DebtsResponse> debtsRef = new AtomicReference<>();
        AtomicReference<Throwable> errorRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(2);

        riskServiceClient.getCreditScore(
                ScoreRequest.newBuilder().setCedula(request.cedula).build()
        ).subscribe().with(
                score -> { scoreRef.set(score); latch.countDown(); },
                err   -> { errorRef.set(err);   latch.countDown(); }
        );

        riskServiceClient.getDebts(
                DebtsRequest.newBuilder().setCedula(request.cedula).build()
        ).subscribe().with(
                debts -> { debtsRef.set(debts); latch.countDown(); },
                err   -> { errorRef.set(err);   latch.countDown(); }
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupción al esperar respuesta del servicio de riesgos", e);
        }

        if (errorRef.get() != null) {
            throw new RuntimeException(
                    "Error al consultar el servicio de riesgos: " + errorRef.get().getMessage(),
                    errorRef.get());
        }

        int score = scoreRef.get().getScore();
        double deudaMensual = debtsRef.get().getTotalMonthlyDebt();

        // Regla de negocio: APROBADO si score > 70 Y (deuda + cuota) < salario * 40%
        double cuotaEstimada = request.montoSolicitado / (request.plazoAnios * 12.0);
        double capacidadPago = request.salario * 0.40;
        boolean aprobado = score > 70 && (deudaMensual + cuotaEstimada) < capacidadPago;

        String motivo = null;
        if (!aprobado) {
            if (score <= 70) {
                motivo = "Score crediticio insuficiente: " + score + " (mínimo requerido: 71)";
            } else {
                motivo = String.format(
                        "Capacidad de pago insuficiente: deuda mensual + cuota (%.2f) supera el 40%% del salario (%.2f)",
                        deudaMensual + cuotaEstimada, capacidadPago);
            }
        }

        CreditEvaluation evaluation = new CreditEvaluation();
        evaluation.cedula = request.cedula;
        evaluation.montoSolicitado = request.montoSolicitado;
        evaluation.plazoAnios = request.plazoAnios;
        evaluation.salario = request.salario;
        evaluation.scoreObtenido = score;
        evaluation.deudaMensual = deudaMensual;
        evaluation.estado = aprobado ? EvaluationStatus.APROBADO : EvaluationStatus.RECHAZADO;
        evaluation.fechaEvaluacion = LocalDateTime.now();
        evaluation.motivoRechazo = motivo;
        evaluation.persist();

        CreditEvaluationResponse response = mapper.toResponse(evaluation);
        response.deudas = debtsRef.get().getDebtsList().stream()
                .map(d -> new CreditEvaluationResponse.DebtDetail(d.getDescription(), d.getMonthlyPayment()))
                .collect(Collectors.toList());

        return response;
    }

    @Override
    public List<CreditEvaluationResponse> findAll() {
        return CreditEvaluation.<CreditEvaluation>listAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
