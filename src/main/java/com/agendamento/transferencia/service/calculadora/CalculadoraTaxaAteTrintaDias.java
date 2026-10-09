package com.agendamento.transferencia.service.calculadora;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculadoraTaxaAteTrintaDias implements CalculadoraTaxa {

    private static final BigDecimal PERCENTUAL = new BigDecimal("0.069");

    @Override
    public boolean aplica(long dias) {
        return dias >= 21 && dias <= 30;
    }

    @Override
    public BigDecimal calcular(BigDecimal valor) {
        return valor.multiply(PERCENTUAL).setScale(2, RoundingMode.HALF_UP);
    }
}
