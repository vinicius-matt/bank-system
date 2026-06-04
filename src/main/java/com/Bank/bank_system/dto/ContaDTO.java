package com.Bank.bank_system.dto;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.model.TipoConta;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContaDTO {


    private Long clienteId;
    private TipoConta tipoConta;
}
