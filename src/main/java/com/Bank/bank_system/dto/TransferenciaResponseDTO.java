package com.Bank.bank_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TransferenciaResponseDTO {
    private Long origemId;
    private String origemNumero;
    private BigDecimal saldoOrigem;
    private Long destinoId;
    private String destinoNumero;
    private BigDecimal valor;
    private String mensagem;
    private LocalDateTime data;
}
