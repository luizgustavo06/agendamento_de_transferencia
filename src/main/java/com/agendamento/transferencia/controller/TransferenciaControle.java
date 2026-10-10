package com.agendamento.transferencia.controller;

import com.agendamento.transferencia.dto.TransferenciaRequisicaoDTO;
import com.agendamento.transferencia.dto.TransferenciaRespostaDTO;
import com.agendamento.transferencia.service.TransferenciaServico;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/transferencias")
public class TransferenciaControle {

    private final TransferenciaServico transferenciaServico;

    public TransferenciaControle(TransferenciaServico transferenciaServico) {
        this.transferenciaServico = transferenciaServico;
    }

    @PostMapping
    public ResponseEntity<TransferenciaRespostaDTO> agendar(@Valid @RequestBody TransferenciaRequisicaoDTO requisicao) {
        TransferenciaRespostaDTO resposta = transferenciaServico.agendar(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public ResponseEntity<List<TransferenciaRespostaDTO>> listarTodas() {
        List<TransferenciaRespostaDTO> lista = transferenciaServico.listarTodas();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferenciaRespostaDTO> buscarPorId(@PathVariable Long id) {
        TransferenciaRespostaDTO resposta = transferenciaServico.buscarPorId(id);
        return ResponseEntity.ok(resposta);
    }
}
