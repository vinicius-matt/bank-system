package com.Bank.bank_system.dto;

import com.Bank.bank_system.model.TipoChavePix;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChavePixResponseDTO {
    private Long id;
    private TipoChavePix tipo;
    private String valor;
    private Long contaId;
    private String numeroConta;
}
