package com.agendamento.transferencia.controller;

import com.agendamento.transferencia.dto.TransferenciaRequisicaoDTO;
import com.agendamento.transferencia.dto.TransferenciaRespostaDTO;
import com.agendamento.transferencia.exception.RecursoNaoEncontradoException;
import com.agendamento.transferencia.exception.TaxaNaoAplicavelException;
import com.agendamento.transferencia.service.TransferenciaServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransferenciaControleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferenciaServico transferenciaServico;

    @Test
    void deveAgendarTransferenciaComSucessoRetornando201() throws Exception {
        TransferenciaRequisicaoDTO requisicao = new TransferenciaRequisicaoDTO(
                "1234567890",
                "0987654321",
                new BigDecimal("1000.00"),
                LocalDate.now().plusDays(2)
        );

        TransferenciaRespostaDTO resposta = new TransferenciaRespostaDTO(
                1L,
                "1234567890",
                "0987654321",
                new BigDecimal("1000.00"),
                new BigDecimal("12.00"),
                new BigDecimal("1012.00"),
                LocalDate.now(),
                LocalDate.now().plusDays(2)
        );

        when(transferenciaServico.agendar(any())).thenReturn(resposta);

        mockMvc.perform(post("/api/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.contaOrigem").value("1234567890"))
                .andExpect(jsonPath("$.valor").value(1000.00))
                .andExpect(jsonPath("$.taxa").value(12.00))
                .andExpect(jsonPath("$.valorTotal").value(1012.00));
    }

    @Test
    void deveRetornar400QuandoRequisicaoInvalida() throws Exception {
        TransferenciaRequisicaoDTO requisicaoInvalida = new TransferenciaRequisicaoDTO(
                "123",
                "",
                new BigDecimal("-50.00"),
                null
        );

        mockMvc.perform(post("/api/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicaoInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validacoes").isArray());
    }

    @Test
    void deveRetornar422QuandoTaxaNaoAplicavel() throws Exception {
        TransferenciaRequisicaoDTO requisicao = new TransferenciaRequisicaoDTO(
                "1234567890",
                "0987654321",
                new BigDecimal("500.00"),
                LocalDate.now().plusDays(60)
        );

        when(transferenciaServico.agendar(any()))
                .thenThrow(new TaxaNaoAplicavelException("Não há taxa aplicável para o intervalo de dias."));

        mockMvc.perform(post("/api/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensagem").value("Não há taxa aplicável para o intervalo de dias."));
    }

    @Test
    void deveListarTodasTransferenciasComSucessoRetornando200() throws Exception {
        TransferenciaRespostaDTO item = new TransferenciaRespostaDTO(
                1L,
                "1234567890",
                "0987654321",
                new BigDecimal("100.00"),
                new BigDecimal("12.00"),
                new BigDecimal("112.00"),
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );

        when(transferenciaServico.listarTodas()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/transferencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].contaOrigem").value("1234567890"));
    }

    @Test
    void deveRetornar404QuandoBuscarIdInexistente() throws Exception {
        when(transferenciaServico.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Transferência com id 99 não encontrada."));

        mockMvc.perform(get("/api/transferencias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Transferência com id 99 não encontrada."));
    }
}
