package com.Bank.NimbusBank.dto;


import com.Bank.NimbusBank.model.TipoConta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContaDTO {


    private Long clienteId;
    private TipoConta tipoConta;
}
