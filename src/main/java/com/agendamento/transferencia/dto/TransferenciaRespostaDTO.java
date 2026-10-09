package com.agendamento.transferencia.dto;

import com.agendamento.transferencia.model.Transferencia;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferenciaRespostaDTO {

    private Long id;
    private String contaOrigem;
    private String contaDestino;
    private BigDecimal valor;
    private BigDecimal taxa;
    private BigDecimal valorTotal;
    private LocalDate dataAgendamento;
    private LocalDate dataTransferencia;

    public TransferenciaRespostaDTO() {
    }

    public TransferenciaRespostaDTO(Long id, String contaOrigem, String contaDestino, BigDecimal valor, BigDecimal taxa, BigDecimal valorTotal, LocalDate dataAgendamento, LocalDate dataTransferencia) {
        this.id = id;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.taxa = taxa;
        this.valorTotal = valorTotal;
        this.dataAgendamento = dataAgendamento;
        this.dataTransferencia = dataTransferencia;
    }

    public static TransferenciaRespostaDTO aPartirDe(Transferencia transferencia) {
        return new TransferenciaRespostaDTO(
                transferencia.getId(),
                transferencia.getContaOrigem(),
                transferencia.getContaDestino(),
                transferencia.getValor(),
                transferencia.getTaxa(),
                transferencia.calcularValorTotal(),
                transferencia.getDataAgendamento(),
                transferencia.getDataTransferencia()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(String contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    public String getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(String contaDestino) {
        this.contaDestino = contaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalDate getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(LocalDate dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }
}
