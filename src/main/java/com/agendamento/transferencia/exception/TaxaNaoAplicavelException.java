package com.agendamento.transferencia.exception;

public class TaxaNaoAplicavelException extends RuntimeException {

    public TaxaNaoAplicavelException(String mensagem) {
        super(mensagem);
    }
}
