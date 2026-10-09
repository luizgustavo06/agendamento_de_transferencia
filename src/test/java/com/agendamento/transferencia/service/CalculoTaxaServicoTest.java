package com.agendamento.transferencia.service;

import com.agendamento.transferencia.exception.TaxaNaoAplicavelException;
import com.agendamento.transferencia.service.calculadora.CalculadoraTaxa;
import com.agendamento.transferencia.service.calculadora.CalculadoraTaxaAteCinquentaDias;
import com.agendamento.transferencia.service.calculadora.CalculadoraTaxaAteDezDias;
import com.agendamento.transferencia.service.calculadora.CalculadoraTaxaAteQuarentaDias;
import com.agendamento.transferencia.service.calculadora.CalculadoraTaxaAteTrintaDias;
import com.agendamento.transferencia.service.calculadora.CalculadoraTaxaAteVinteDias;
import com.agendamento.transferencia.service.calculadora.CalculadoraTaxaMesmoDia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculoTaxaServicoTest {

    private CalculoTaxaServico calculoTaxaServico;

    @BeforeEach
    void inicializar() {
        List<CalculadoraTaxa> calculadoras = List.of(
                new CalculadoraTaxaMesmoDia(),
                new CalculadoraTaxaAteDezDias(),
                new CalculadoraTaxaAteVinteDias(),
                new CalculadoraTaxaAteTrintaDias(),
                new CalculadoraTaxaAteQuarentaDias(),
                new CalculadoraTaxaAteCinquentaDias()
        );
        calculoTaxaServico = new CalculoTaxaServico(calculadoras);
    }

    @Test
    void deveCalcularTaxaParaMesmoDia() {
        LocalDate hoje = LocalDate.now();
        BigDecimal taxa = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje);
        assertEquals(new BigDecimal("28.00"), taxa);
    }

    @Test
    void deveCalcularTaxaParaIntervaloDeUmADezDias() {
        LocalDate hoje = LocalDate.now();
        BigDecimal taxa = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(5));
        assertEquals(new BigDecimal("12.00"), taxa);

        BigDecimal taxaLimite = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(10));
        assertEquals(new BigDecimal("12.00"), taxaLimite);
    }

    @Test
    void deveCalcularTaxaParaIntervaloDeOnzeAVinteDias() {
        LocalDate hoje = LocalDate.now();
        BigDecimal taxa = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(15));
        assertEquals(new BigDecimal("82.00"), taxa);

        BigDecimal taxaLimite = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(20));
        assertEquals(new BigDecimal("82.00"), taxaLimite);
    }

    @Test
    void deveCalcularTaxaParaIntervaloDeVinteUmATrintaDias() {
        LocalDate hoje = LocalDate.now();
        BigDecimal taxa = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(25));
        assertEquals(new BigDecimal("69.00"), taxa);

        BigDecimal taxaLimite = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(30));
        assertEquals(new BigDecimal("69.00"), taxaLimite);
    }

    @Test
    void deveCalcularTaxaParaIntervaloDeTrintaUmAQuarentaDias() {
        LocalDate hoje = LocalDate.now();
        BigDecimal taxa = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(35));
        assertEquals(new BigDecimal("47.00"), taxa);

        BigDecimal taxaLimite = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(40));
        assertEquals(new BigDecimal("47.00"), taxaLimite);
    }

    @Test
    void deveCalcularTaxaParaIntervaloDeQuarentaUmACinquentaDias() {
        LocalDate hoje = LocalDate.now();
        BigDecimal taxa = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(45));
        assertEquals(new BigDecimal("17.00"), taxa);

        BigDecimal taxaLimite = calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(50));
        assertEquals(new BigDecimal("17.00"), taxaLimite);
    }

    @Test
    void deveLancarExcecaoQuandoDataNoPassado() {
        LocalDate hoje = LocalDate.now();
        assertThrows(TaxaNaoAplicavelException.class, () ->
                calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.minusDays(1))
        );
    }

    @Test
    void deveLancarExcecaoQuandoDiasSuperiorACinquenta() {
        LocalDate hoje = LocalDate.now();
        assertThrows(TaxaNaoAplicavelException.class, () ->
                calculoTaxaServico.calcular(new BigDecimal("1000.00"), hoje, hoje.plusDays(51))
        );
    }
}
