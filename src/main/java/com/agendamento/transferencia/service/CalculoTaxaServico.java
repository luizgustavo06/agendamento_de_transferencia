package com.agendamento.transferencia.service;

import com.agendamento.transferencia.exception.TaxaNaoAplicavelException;
import com.agendamento.transferencia.service.calculadora.CalculadoraTaxa;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CalculoTaxaServico {

    private final List<CalculadoraTaxa> calculadoras;

    public CalculoTaxaServico(List<CalculadoraTaxa> calculadoras) {
        this.calculadoras = calculadoras;
    }

    public BigDecimal calcular(BigDecimal valor, LocalDate dataAgendamento, LocalDate dataTransferencia) {
        if (dataAgendamento == null || dataTransferencia == null) {
            throw new TaxaNaoAplicavelException("As datas de agendamento e transferência são obrigatórias.");
        }

        long dias = ChronoUnit.DAYS.between(dataAgendamento, dataTransferencia);

        if (dias < 0) {
            throw new TaxaNaoAplicavelException("Não há taxa aplicável: a data da transferência não pode ser anterior à data de agendamento (" + dias + " dias).");
        }

        if (dias > 50) {
            throw new TaxaNaoAplicavelException("Não há taxa aplicável para transferências com mais de 50 dias de antecedência (" + dias + " dias informados).");
        }

        return calculadoras.stream()
                .filter(calculadora -> calculadora.aplica(dias))
                .findFirst()
                .map(calculadora -> calculadora.calcular(valor))
                .orElseThrow(() -> new TaxaNaoAplicavelException("Não há taxa aplicável para o intervalo de " + dias + " dias."));
    }
}
