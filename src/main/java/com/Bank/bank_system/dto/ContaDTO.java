package com.Bank.bank_system.dto;

import com.Bank.bank_system.model.TipoConta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContaDTO {

    private String numero;
    private Long clienteId;
    private TipoConta tipoConta;
}
