package com.agendamento.transferencia.service;

import com.agendamento.transferencia.dto.TransferenciaRequisicaoDTO;
import com.agendamento.transferencia.dto.TransferenciaRespostaDTO;
import com.agendamento.transferencia.exception.RecursoNaoEncontradoException;
import com.agendamento.transferencia.model.Transferencia;
import com.agendamento.transferencia.repository.TransferenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferenciaServico {

    private final TransferenciaRepository transferenciaRepository;
    private final CalculoTaxaServico calculoTaxaServico;

    public TransferenciaServico(TransferenciaRepository transferenciaRepository, CalculoTaxaServico calculoTaxaServico) {
        this.transferenciaRepository = transferenciaRepository;
        this.calculoTaxaServico = calculoTaxaServico;
    }

    @Transactional
    public TransferenciaRespostaDTO agendar(TransferenciaRequisicaoDTO requisicao) {
        LocalDate dataAgendamento = LocalDate.now();
        BigDecimal taxa = calculoTaxaServico.calcular(
                requisicao.getValor(),
                dataAgendamento,
                requisicao.getDataTransferencia()
        );

        Transferencia transferencia = new Transferencia(
                null,
                requisicao.getContaOrigem(),
                requisicao.getContaDestino(),
                requisicao.getValor(),
                taxa,
                dataAgendamento,
                requisicao.getDataTransferencia()
        );

        Transferencia salva = transferenciaRepository.save(transferencia);
        return TransferenciaRespostaDTO.aPartirDe(salva);
    }

    @Transactional(readOnly = true)
    public List<TransferenciaRespostaDTO> listarTodas() {
        return transferenciaRepository.findAllByOrderByIdDesc()
                .stream()
                .map(TransferenciaRespostaDTO::aPartirDe)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransferenciaRespostaDTO buscarPorId(Long id) {
        return transferenciaRepository.findById(id)
                .map(TransferenciaRespostaDTO::aPartirDe)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transferência com id " + id + " não encontrada."));
    }
}
