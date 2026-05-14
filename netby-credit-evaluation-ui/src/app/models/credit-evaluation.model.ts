export interface CreditEvaluationRequest {
  cedula: string;
  montoSolicitado: number;
  plazoAnios: number;
  salario: number;
}

export interface DebtDetail {
  descripcion: string;
  mensualidad: number;
}

export interface CreditEvaluationResponse {
  id: number;
  cedula: string;
  montoSolicitado: number;
  plazoAnios: number;
  salario: number;
  scoreObtenido: number;
  deudaMensual: number;
  estado: 'APROBADO' | 'RECHAZADO';
  fechaEvaluacion: string;
  motivoRechazo: string | null;
  deudas: DebtDetail[] | null;
}
