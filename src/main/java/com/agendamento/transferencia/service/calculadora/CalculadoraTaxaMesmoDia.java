package com.agendamento.transferencia.service.calculadora;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculadoraTaxaMesmoDia implements CalculadoraTaxa {

    private static final BigDecimal TAXA_FIXA = new BigDecimal("3.00");
    private static final BigDecimal PERCENTUAL = new BigDecimal("0.025");

    @Override
    public boolean aplica(long dias) {
        return dias == 0;
    }

    @Override
    public BigDecimal calcular(BigDecimal valor) {
        return TAXA_FIXA.add(valor.multiply(PERCENTUAL)).setScale(2, RoundingMode.HALF_UP);
    }
}
