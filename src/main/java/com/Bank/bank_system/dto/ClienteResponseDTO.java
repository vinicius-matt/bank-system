package com.Bank.bank_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class ClienteResponseDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
}
