package com.netby.banking.orchestrator.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_evaluations")
public class CreditEvaluation extends PanacheEntity {

    @Column(name = "cedula", nullable = false, length = 10)
    public String cedula;

    @Column(name = "monto_solicitado", nullable = false)
    public Double montoSolicitado;

    @Column(name = "plazo_anios", nullable = false)
    public Integer plazoAnios;

    @Column(name = "salario", nullable = false)
    public Double salario;

    @Column(name = "score_obtenido")
    public Integer scoreObtenido;

    @Column(name = "deuda_mensual")
    public Double deudaMensual;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    public EvaluationStatus estado;

    @Column(name = "fecha_evaluacion", nullable = false)
    public LocalDateTime fechaEvaluacion;

    @Column(name = "motivo_rechazo", length = 500)
    public String motivoRechazo;

    public enum EvaluationStatus {
        APROBADO, RECHAZADO
    }
}
