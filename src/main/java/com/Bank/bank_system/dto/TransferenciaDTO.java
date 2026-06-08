package com.Bank.bank_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class TransferenciaDTO {

    private Long  origemId;
    private Long  destinoId;
    private BigDecimal valor;
    private String mensagem;
}
