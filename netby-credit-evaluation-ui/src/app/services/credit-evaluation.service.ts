import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreditEvaluationRequest, CreditEvaluationResponse } from '../models/credit-evaluation.model';

@Injectable({ providedIn: 'root' })
export class CreditEvaluationService {
  private readonly apiUrl = '/api/v1/credit-evaluations';

  constructor(private http: HttpClient) {}

  evaluate(request: CreditEvaluationRequest): Observable<CreditEvaluationResponse> {
    return this.http.post<CreditEvaluationResponse>(this.apiUrl, request);
  }

  getEvaluations(): Observable<CreditEvaluationResponse[]> {
    return this.http.get<CreditEvaluationResponse[]>(this.apiUrl);
  }
}
