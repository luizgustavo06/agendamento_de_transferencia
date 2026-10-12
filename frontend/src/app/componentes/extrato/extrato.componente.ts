import { Component, OnInit, OnDestroy, inject, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransferenciaServico } from '../../servicos/transferencia.servico';
import { TransferenciaResposta, NotificacaoAlerta } from '../../modelos/transferencia.modelo';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-extrato',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './extrato.componente.html',
  styleUrls: ['./extrato.componente.css']
})
export class ExtratoComponente implements OnInit, OnDestroy {
  private servicoTransferencia = inject(TransferenciaServico);
  private cdr = inject(ChangeDetectorRef);
  private inscricaoAtualizacao?: Subscription;

  @Output() notificacaoEmitida = new EventEmitter<NotificacaoAlerta>();

  transferencias: TransferenciaResposta[] = [];
  carregando: boolean = false;

  get totalValorTransferido(): number {
    return this.transferencias.reduce((acumulado, item) => acumulado + (Number(item.valor) || 0), 0);
  }

  get totalTaxas(): number {
    return this.transferencias.reduce((acumulado, item) => acumulado + (Number(item.taxa) || 0), 0);
  }

  ngOnInit(): void {
    this.carregarTransferencias();

    this.inscricaoAtualizacao = this.servicoTransferencia.obterNotificacaoAtualizacao().subscribe(() => {
      this.carregarTransferencias();
    });
  }

  ngOnDestroy(): void {
    this.inscricaoAtualizacao?.unsubscribe();
  }

  carregarTransferencias(): void {
    this.carregando = true;
    this.cdr.detectChanges();

    this.servicoTransferencia.listarTodas().subscribe({
      next: (dados) => {
        this.transferencias = Array.isArray(dados) ? dados : [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.carregando = false;
        this.cdr.detectChanges();
        this.notificacaoEmitida.emit({
          tipo: 'erro',
          titulo: 'Erro ao Carregar Extrato',
          mensagem: 'Não foi possível carregar a lista de transferências do servidor. Verifique a conexão com a API.'
        });
      }
    });
  }

  formatarData(dataVal: any): string {
    if (!dataVal) return '-';
    if (Array.isArray(dataVal)) {
      const [ano, mes, dia] = dataVal;
      return `${String(dia).padStart(2, '0')}/${String(mes).padStart(2, '0')}/${ano}`;
    }
    const dataStr = String(dataVal);
    const partes = dataStr.split('-');
    if (partes.length === 3) {
      return `${partes[2]}/${partes[1]}/${partes[0]}`;
    }
    return dataStr;
  }
}
