package com.Bank.bank_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ClienteUpdateDTO {

    private String email;
    private String celular;
    private String Nome;

}
