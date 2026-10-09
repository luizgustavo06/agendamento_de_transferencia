package com.agendamento.transferencia.service.calculadora;

import java.math.BigDecimal;

public interface CalculadoraTaxa {

    boolean aplica(long dias);

    BigDecimal calcular(BigDecimal valor);
}
