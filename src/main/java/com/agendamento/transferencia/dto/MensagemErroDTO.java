package com.agendamento.transferencia.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MensagemErroDTO {

    private LocalDateTime dataHora;
    private Integer status;
    private String erro;
    private String mensagem;
    private String caminho;
    private List<CampoValidacaoDTO> validacoes;

    public MensagemErroDTO() {
    }

    public MensagemErroDTO(LocalDateTime dataHora, Integer status, String erro, String mensagem, String caminho) {
        this.dataHora = dataHora;
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
    }

    public MensagemErroDTO(LocalDateTime dataHora, Integer status, String erro, String mensagem, String caminho, List<CampoValidacaoDTO> validacoes) {
        this.dataHora = dataHora;
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
        this.validacoes = validacoes;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public List<CampoValidacaoDTO> getValidacoes() {
        return validacoes;
    }

    public void setValidacoes(List<CampoValidacaoDTO> validacoes) {
        this.validacoes = validacoes;
    }
}
