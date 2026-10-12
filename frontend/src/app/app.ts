import { Component, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlertaComponente } from './componentes/alerta/alerta.componente';
import { AgendamentoComponente } from './componentes/agendamento/agendamento.componente';
import { ExtratoComponente } from './componentes/extrato/extrato.componente';
import { NotificacaoAlerta } from './modelos/transferencia.modelo';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, AlertaComponente, AgendamentoComponente, ExtratoComponente],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  private cdr = inject(ChangeDetectorRef);
  notificacaoAtual: NotificacaoAlerta | null = null;
  dataAtual: string = new Date().toLocaleDateString('pt-BR');

  receberNotificacao(alerta: NotificacaoAlerta): void {
    this.notificacaoAtual = alerta;
    this.cdr.detectChanges();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  limparNotificacao(): void {
    this.notificacaoAtual = null;
    this.cdr.detectChanges();
  }
}
