export interface TransferenciaRequisicao {
  contaOrigem: string;
  contaDestino: string;
  valor: number;
  dataTransferencia: string;
}

export interface TransferenciaResposta {
  id: number;
  contaOrigem: string;
  contaDestino: string;
  valor: number;
  taxa: number;
  valorTotal: number;
  dataAgendamento: string;
  dataTransferencia: string;
}

export interface CampoValidacao {
  campo: string;
  mensagem: string;
}

export interface MensagemErro {
  dataHora: string;
  status: number;
  erro: string;
  mensagem: string;
  caminho: string;
  validacoes?: CampoValidacao[];
}

export interface NotificacaoAlerta {
  tipo: 'sucesso' | 'aviso' | 'erro' | 'info';
  titulo: string;
  mensagem: string;
  detalhes?: string[];
}
