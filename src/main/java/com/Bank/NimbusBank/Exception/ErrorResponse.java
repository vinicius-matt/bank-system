package com.Bank.NimbusBank.Exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private String mensagem;

    public ErrorResponse(String mensagem) {
        this.mensagem = mensagem;
    }
}