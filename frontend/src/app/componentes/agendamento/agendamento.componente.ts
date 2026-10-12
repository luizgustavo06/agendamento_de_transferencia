import { Component, Output, EventEmitter, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TransferenciaServico } from '../../servicos/transferencia.servico';
import { NotificacaoAlerta, TransferenciaRequisicao } from '../../modelos/transferencia.modelo';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-agendamento',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './agendamento.componente.html',
  styleUrls: ['./agendamento.componente.css']
})
export class AgendamentoComponente implements OnInit {
  private construtorFormulario = inject(FormBuilder);
  private servicoTransferencia = inject(TransferenciaServico);
  private cdr = inject(ChangeDetectorRef);

  @Output() notificacaoEmitida = new EventEmitter<NotificacaoAlerta>();

  formulario!: FormGroup;
  carregando: boolean = false;
  dataMinima: string = '';
  valorExibicao: string = '';

  previaTaxa: {
    dias: number;
    taxaEstimada: number | null;
    valorTotalEstimado: number | null;
    mensagemTaxa: string;
    aplicavel: boolean;
  } | null = null;

  ngOnInit(): void {
    const hoje = new Date();
    this.dataMinima = hoje.toISOString().split('T')[0];

    this.formulario = this.construtorFormulario.group({
      contaOrigem: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      contaDestino: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      valor: [null, [Validators.required, Validators.min(0.01)]],
      dataTransferencia: [this.dataMinima, [Validators.required]]
    }, {
      validators: [this.validarContasDiferentes]
    });

    this.formulario.get('dataTransferencia')?.valueChanges.subscribe(() => {
      this.atualizarPreviaTaxa();
      this.cdr.detectChanges();
    });

    this.atualizarPreviaTaxa();
  }

  validarContasDiferentes(grupo: FormGroup) {
    const origem = grupo.get('contaOrigem')?.value;
    const destino = grupo.get('contaDestino')?.value;
    if (origem && destino && origem === destino) {
      return { contasIguais: true };
    }
    return null;
  }

  aoDigitarMoeda(evento: Event): void {
    const elementoInput = evento.target as HTMLInputElement;
    let texto = elementoInput.value;

    if (!texto || texto.trim() === '') {
      this.valorExibicao = '';
      elementoInput.value = '';
      this.formulario.get('valor')?.setValue(null);
      this.atualizarPreviaTaxa();
      this.cdr.detectChanges();
      return;
    }

    if (texto.endsWith('.')) {
      texto = texto.slice(0, -1) + ',';
    }

    let temVirgula = texto.includes(',');
    let parteInteiraBruta = '';
    let parteDecimalBruta: string | null = null;

    if (temVirgula) {
      const pedacos = texto.split(',');
      parteInteiraBruta = pedacos[0].replace(/\D/g, '');
      parteDecimalBruta = pedacos.slice(1).join('').replace(/\D/g, '').substring(0, 2);
    } else {
      if (texto.includes('.') && !/^\d{1,3}(\.\d{3})+$/.test(texto)) {
        const pedacos = texto.split('.');
        if (pedacos.length === 2 && pedacos[1].length <= 2) {
          parteInteiraBruta = pedacos[0].replace(/\D/g, '');
          parteDecimalBruta = pedacos[1].replace(/\D/g, '').substring(0, 2);
          temVirgula = true;
        } else {
          parteInteiraBruta = texto.replace(/\D/g, '');
        }
      } else {
        parteInteiraBruta = texto.replace(/\D/g, '');
      }
    }

    if (parteInteiraBruta.length > 1 && parteInteiraBruta.startsWith('0')) {
      parteInteiraBruta = parteInteiraBruta.replace(/^0+/, '') || '0';
    }

    if (!parteInteiraBruta && parteDecimalBruta === null) {
      this.valorExibicao = '';
      elementoInput.value = '';
      this.formulario.get('valor')?.setValue(null);
      this.atualizarPreviaTaxa();
      this.cdr.detectChanges();
      return;
    }

    const inteiraFormatada = parteInteiraBruta
      ? parteInteiraBruta.replace(/\B(?=(\d{3})+(?!\d))/g, '.')
      : '0';

    if (temVirgula) {
      this.valorExibicao = `${inteiraFormatada},${parteDecimalBruta ?? ''}`;
    } else {
      this.valorExibicao = inteiraFormatada;
    }

    elementoInput.value = this.valorExibicao;

    const decimalFinal = parteDecimalBruta !== null && parteDecimalBruta !== '' ? parteDecimalBruta : '00';
    const numeroFinal = parseFloat(`${parteInteiraBruta || '0'}.${decimalFinal}`);

    if (!isNaN(numeroFinal) && numeroFinal > 0) {
      this.formulario.get('valor')?.setValue(numeroFinal);
      this.formulario.get('valor')?.markAsDirty();
    } else {
      this.formulario.get('valor')?.setValue(null);
    }

    this.atualizarPreviaTaxa();
    this.cdr.detectChanges();
  }

  aoSairDoCampoMoeda(): void {
    const valorAtual = this.formulario.get('valor')?.value;

    if (valorAtual && !isNaN(valorAtual) && Number(valorAtual) > 0) {
      this.valorExibicao = Number(valorAtual).toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    } else {
      this.valorExibicao = '';
      this.formulario.get('valor')?.setValue(null);
    }

    this.formulario.get('valor')?.markAsTouched();
    this.atualizarPreviaTaxa();
    this.cdr.detectChanges();
  }

  atualizarPreviaTaxa(): void {
    const valor = Number(this.formulario.get('valor')?.value);
    const dataTransferenciaStr = this.formulario.get('dataTransferencia')?.value;

    if (!dataTransferenciaStr || isNaN(valor) || valor <= 0) {
      this.previaTaxa = null;
      return;
    }

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    const [ano, mes, dia] = dataTransferenciaStr.split('-').map(Number);
    const dataAlvo = new Date(ano, mes - 1, dia);
    dataAlvo.setHours(0, 0, 0, 0);

    const diferencaMilissegundos = dataAlvo.getTime() - hoje.getTime();
    const dias = Math.round(diferencaMilissegundos / (1000 * 60 * 60 * 24));

    if (dias < 0) {
      this.previaTaxa = {
        dias,
        taxaEstimada: null,
        valorTotalEstimado: null,
        mensagemTaxa: 'A data não pode ser anterior a hoje.',
        aplicavel: false
      };
      return;
    }

    if (dias > 50) {
      this.previaTaxa = {
        dias,
        taxaEstimada: null,
        valorTotalEstimado: null,
        mensagemTaxa: 'Prazo superior a 50 dias: não há taxa aplicável.',
        aplicavel: false
      };
      return;
    }

    let taxaCalculada = 0;
    let descricaoRegra = '';

    if (dias === 0) {
      taxaCalculada = 3.00 + (valor * 0.025);
      descricaoRegra = 'Faixa 0 dias: R$ 3,00 + 2,5%';
    } else if (dias >= 1 && dias <= 10) {
      taxaCalculada = 12.00;
      descricaoRegra = 'Faixa 1 a 10 dias: R$ 12,00 fixo';
    } else if (dias >= 11 && dias <= 20) {
      taxaCalculada = valor * 0.082;
      descricaoRegra = 'Faixa 11 a 20 dias: 8,2%';
    } else if (dias >= 21 && dias <= 30) {
      taxaCalculada = valor * 0.069;
      descricaoRegra = 'Faixa 21 a 30 dias: 6,9%';
    } else if (dias >= 31 && dias <= 40) {
      taxaCalculada = valor * 0.047;
      descricaoRegra = 'Faixa 31 a 40 dias: 4,7%';
    } else if (dias >= 41 && dias <= 50) {
      taxaCalculada = valor * 0.017;
      descricaoRegra = 'Faixa 41 a 50 dias: 1,7%';
    }

    this.previaTaxa = {
      dias,
      taxaEstimada: Number(taxaCalculada.toFixed(2)),
      valorTotalEstimado: Number((valor + taxaCalculada).toFixed(2)),
      mensagemTaxa: descricaoRegra,
      aplicavel: true
    };
  }

  submeterAgendamento(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.notificacaoEmitida.emit({
        tipo: 'aviso',
        titulo: 'Campos Inválidos',
        mensagem: 'Verifique o formato das contas (10 dígitos numéricos) e o valor antes de prosseguir.'
      });
      return;
    }

    this.carregando = true;
    this.cdr.detectChanges();

    const requisicao: TransferenciaRequisicao = {
      contaOrigem: this.formulario.value.contaOrigem,
      contaDestino: this.formulario.value.contaDestino,
      valor: Number(this.formulario.value.valor),
      dataTransferencia: this.formulario.value.dataTransferencia
    };

    this.servicoTransferencia.agendar(requisicao).subscribe({
      next: (resposta) => {
        this.carregando = false;
        this.valorExibicao = '';
        this.formulario.reset({
          contaOrigem: '',
          contaDestino: '',
          valor: null,
          dataTransferencia: this.dataMinima
        });
        this.atualizarPreviaTaxa();

        this.servicoTransferencia.notificarAtualizacao();

        this.notificacaoEmitida.emit({
          tipo: 'sucesso',
          titulo: 'Agendamento Concluído!',
          mensagem: `Transferência de R$ ${resposta.valor.toFixed(2)} agendada com sucesso com taxa de R$ ${resposta.taxa.toFixed(2)}.`
        });
        this.cdr.detectChanges();
      },
      error: (erro: HttpErrorResponse) => {
        this.carregando = false;
        this.tratarErroResposta(erro);
        this.cdr.detectChanges();
      }
    });
  }

  private tratarErroResposta(erro: HttpErrorResponse): void {
    if (erro.status === 422) {
      this.notificacaoEmitida.emit({
        tipo: 'aviso',
        titulo: 'Regra de Taxa Não Aplicável (422)',
        mensagem: erro.error?.mensagem || 'Não há taxa aplicável para o período informado. O limite máximo é de 50 dias.'
      });
    } else if (erro.status === 400) {
      const detalhes: string[] = [];
      if (erro.error?.validacoes && Array.isArray(erro.error.validacoes)) {
        for (const item of erro.error.validacoes) {
          detalhes.push(`${item.campo}: ${item.mensagem}`);
        }
      }

      this.notificacaoEmitida.emit({
        tipo: 'erro',
        titulo: 'Dados Inválidos (400)',
        mensagem: erro.error?.mensagem || 'Verifique o formato das contas e o valor.',
        detalhes: detalhes.length > 0 ? detalhes : undefined
      });
    } else if (erro.status === 404) {
      this.notificacaoEmitida.emit({
        tipo: 'erro',
        titulo: 'Recurso Não Encontrado (404)',
        mensagem: erro.error?.mensagem || 'O recurso solicitado não foi encontrado no servidor.'
      });
    } else {
      this.notificacaoEmitida.emit({
        tipo: 'erro',
        titulo: 'Falha de Comunicação (500)',
        mensagem: 'Erro de comunicação com o servidor. Verifique se a API backend está em execução.'
      });
    }
  }

  apenasNumeros(evento: KeyboardEvent): boolean {
    const tecla = evento.key;
    if (['Backspace', 'Tab', 'ArrowLeft', 'ArrowRight', 'Delete'].includes(tecla)) {
      return true;
    }
    return /^\d$/.test(tecla);
  }
}
