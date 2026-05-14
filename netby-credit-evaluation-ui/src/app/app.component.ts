import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CreditEvaluationService } from './services/credit-evaluation.service';
import { CreditEvaluationResponse } from './models/credit-evaluation.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  form: FormGroup;
  loading = false;
  loadingList = false;
  result: CreditEvaluationResponse | null = null;
  error: string | null = null;
  evaluations: CreditEvaluationResponse[] = [];

  constructor(
    private fb: FormBuilder,
    private evaluationService: CreditEvaluationService,
    private cdr: ChangeDetectorRef,
    private zone: NgZone
  ) {
    this.form = this.fb.group({
      cedula: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      montoSolicitado: ['', [Validators.required, Validators.min(1)]],
      plazoAnios: ['', [Validators.required, Validators.min(1), Validators.max(30)]],
      salario: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    // No carga el historial al inicio — requiere cédula válida
  }

  get f() { return this.form.controls; }

  get cedulaValid(): boolean {
    return this.f['cedula'].valid;
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.result = null;
    this.error = null;

    this.evaluationService.evaluate(this.form.value).subscribe({
      next: (res) => {
        this.zone.run(() => {
          this.result = res;
          this.loading = false;
          this.loadEvaluations();
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.zone.run(() => {
          this.loading = false;
          if (err.error?.error) {
            this.error = err.error.error;
          } else if (err.error?.detalles) {
            this.error = err.error.detalles;
          } else {
            this.error = 'Error al comunicarse con el servidor. Intente nuevamente.';
          }
          this.cdr.detectChanges();
        });
      }
    });
  }

  loadEvaluations(): void {
    if (!this.cedulaValid) {
      this.evaluations = [];
      return;
    }
    this.loadingList = true;
    const cedula = this.f['cedula'].value;

    this.evaluationService.getEvaluations().subscribe({
      next: (data) => {
        this.zone.run(() => {
          this.evaluations = data
            .filter(e => e.cedula === cedula)
            .sort((a, b) => new Date(b.fechaEvaluacion).getTime() - new Date(a.fechaEvaluacion).getTime());
          this.loadingList = false;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.zone.run(() => {
          this.loadingList = false;
          this.cdr.detectChanges();
        });
      }
    });
  }
}
