package com.netby.banking.risk.application.service;

import io.smallrye.mutiny.Uni;
import com.netby.banking.risk.grpc.Debt;
import com.netby.banking.risk.grpc.DebtsResponse;
import com.netby.banking.risk.grpc.ScoreResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Lógica de negocio del servicio de riesgos.
 * Simula la consulta a un buró de crédito externo.
 */
@ApplicationScoped
public class RiskCalculationService {

    private final Random random = new Random();

    private static final String[] DEBT_DESCRIPTIONS = {
            "Tarjeta de Crédito", "Préstamo Personal",
            "Crédito Hipotecario", "Crédito Vehicular"
    };

    public ScoreResponse calculateScore(String cedula) {
        int score = random.nextInt(101); // 0-100
        return com.netby.banking.risk.grpc.ScoreResponse.newBuilder()
                .setCedula(cedula)
                .setScore(score)
                .build();
    }

    public DebtsResponse calculateDebts(String cedula) {
        List<Debt> debts = new ArrayList<>();
        int numDebts = random.nextInt(4); // 0-3 deudas
        double total = 0;

        for (int i = 0; i < numDebts; i++) {
            double monthly = Math.round((random.nextDouble() * 400 + 50) * 100.0) / 100.0;
            total += monthly;
            debts.add(Debt.newBuilder()
                    .setDescription(DEBT_DESCRIPTIONS[i % DEBT_DESCRIPTIONS.length])
                    .setMonthlyPayment(monthly)
                    .build());
        }

        return DebtsResponse.newBuilder()
                .setCedula(cedula)
                .addAllDebts(debts)
                .setTotalMonthlyDebt(Math.round(total * 100.0) / 100.0)
                .build();
    }
}
