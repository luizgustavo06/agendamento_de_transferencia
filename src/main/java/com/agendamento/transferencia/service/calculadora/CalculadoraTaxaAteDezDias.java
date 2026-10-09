package com.agendamento.transferencia.service.calculadora;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculadoraTaxaAteDezDias implements CalculadoraTaxa {

    private static final BigDecimal TAXA_FIXA = new BigDecimal("12.00");

    @Override
    public boolean aplica(long dias) {
        return dias >= 1 && dias <= 10;
    }

    @Override
    public BigDecimal calcular(BigDecimal valor) {
        return TAXA_FIXA.setScale(2, RoundingMode.HALF_UP);
    }
}
