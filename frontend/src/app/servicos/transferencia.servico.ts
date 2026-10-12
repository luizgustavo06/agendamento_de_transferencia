import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { TransferenciaRequisicao, TransferenciaResposta } from '../modelos/transferencia.modelo';

@Injectable({
  providedIn: 'root'
})
export class TransferenciaServico {
  private http = inject(HttpClient);
  private urlApi = 'http://localhost:8080/api/transferencias';
  private atualizacaoExtrato$ = new Subject<void>();

  agendar(requisicao: TransferenciaRequisicao): Observable<TransferenciaResposta> {
    return this.http.post<TransferenciaResposta>(this.urlApi, requisicao);
  }

  listarTodas(): Observable<TransferenciaResposta[]> {
    return this.http.get<TransferenciaResposta[]>(this.urlApi);
  }

  notificarAtualizacao(): void {
    this.atualizacaoExtrato$.next();
  }

  obterNotificacaoAtualizacao(): Observable<void> {
    return this.atualizacaoExtrato$.asObservable();
  }
}
