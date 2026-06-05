package com.Bank.bank_system.dto;

import com.Bank.bank_system.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransacaoResponseDTO {

    private Long id;
    private TransactionType tipo;
    private BigDecimal valor;
    private String descricao;
    private LocalDateTime data;
}

