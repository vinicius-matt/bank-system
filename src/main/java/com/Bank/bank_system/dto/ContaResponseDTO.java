package com.Bank.bank_system.dto;

import com.Bank.bank_system.model.StatusConta;
import com.Bank.bank_system.model.TipoConta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ContaResponseDTO {
    private Long id;
    private String numero;
    private BigDecimal saldo;
    private BigDecimal limite;
    private TipoConta tipo;
    private StatusConta status;
    private Long clienteId;
}
