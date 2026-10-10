package com.agendamento.transferencia.exception;

import com.agendamento.transferencia.dto.CampoValidacaoDTO;
import com.agendamento.transferencia.dto.MensagemErroDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ManipuladorExcecoesGlobal {

    @ExceptionHandler(TaxaNaoAplicavelException.class)
    public ResponseEntity<MensagemErroDTO> tratarTaxaNaoAplicavel(TaxaNaoAplicavelException excecao, HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        MensagemErroDTO erro = new MensagemErroDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                excecao.getMessage(),
                requisicao.getRequestURI()
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MensagemErroDTO> tratarValidacao(MethodArgumentNotValidException excecao, HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<CampoValidacaoDTO> validacoes = new ArrayList<>();

        for (FieldError erroCampo : excecao.getBindingResult().getFieldErrors()) {
            validacoes.add(new CampoValidacaoDTO(erroCampo.getField(), erroCampo.getDefaultMessage()));
        }

        MensagemErroDTO erro = new MensagemErroDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Erro de validação nos campos da requisição.",
                requisicao.getRequestURI(),
                validacoes
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<MensagemErroDTO> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException excecao, HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        MensagemErroDTO erro = new MensagemErroDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                excecao.getMessage(),
                requisicao.getRequestURI()
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensagemErroDTO> tratarErroInesperado(Exception excecao, HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        MensagemErroDTO erro = new MensagemErroDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Ocorreu um erro interno no servidor. Tente novamente mais tarde.",
                requisicao.getRequestURI()
        );
        return ResponseEntity.status(status).body(erro);
    }
}
