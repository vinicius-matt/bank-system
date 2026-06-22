package com.Bank.NimbusBank.dto;

import com.Bank.NimbusBank.model.StatusConta;
import com.Bank.NimbusBank.model.TipoConta;
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
