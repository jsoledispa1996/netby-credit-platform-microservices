package com.netby.banking.risk.infrastructure.grpc;

import com.netby.banking.risk.application.service.RiskCalculationService;
import com.netby.banking.risk.grpc.*;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

import java.time.Duration;

@GrpcService
public class RiskGrpcService implements RiskService {

    @Inject
    RiskCalculationService riskCalculationService;

    @Override
    public Uni<ScoreResponse> getCreditScore(ScoreRequest request) {
        // Simula latencia real de consulta a buró de crédito (~2 segundos)
        return Uni.createFrom()
                .item(() -> riskCalculationService.calculateScore(request.getCedula()))
                .onItem().delayIt().by(Duration.ofMillis(2000));
    }

    @Override
    public Uni<DebtsResponse> getDebts(DebtsRequest request) {
        // Simula latencia real de consulta de deudas (~1.5 segundos)
        return Uni.createFrom()
                .item(() -> riskCalculationService.calculateDebts(request.getCedula()))
                .onItem().delayIt().by(Duration.ofMillis(1500));
    }
}
