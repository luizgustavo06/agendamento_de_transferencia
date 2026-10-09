package com.agendamento.transferencia.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferenciaRequisicaoDTO {

    @NotBlank(message = "A conta de origem é obrigatória.")
    @Pattern(regexp = "^\\d{10}$", message = "A conta de origem deve conter exatamente 10 dígitos numéricos.")
    private String contaOrigem;

    @NotBlank(message = "A conta de destino é obrigatória.")
    @Pattern(regexp = "^\\d{10}$", message = "A conta de destino deve conter exatamente 10 dígitos numéricos.")
    private String contaDestino;

    @NotNull(message = "O valor da transferência é obrigatório.")
    @Positive(message = "O valor da transferência deve ser positivo.")
    private BigDecimal valor;

    @NotNull(message = "A data da transferência é obrigatória.")
    private LocalDate dataTransferencia;

    public TransferenciaRequisicaoDTO() {
    }

    public TransferenciaRequisicaoDTO(String contaOrigem, String contaDestino, BigDecimal valor, LocalDate dataTransferencia) {
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.dataTransferencia = dataTransferencia;
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

    public LocalDate getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(LocalDate dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }
}
