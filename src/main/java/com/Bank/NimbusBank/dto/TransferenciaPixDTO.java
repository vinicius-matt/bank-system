package com.Bank.NimbusBank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferenciaPixDTO {

    @NotNull(message = "Conta de origem é obrigatória")
    private Long origemId;

    @NotBlank(message = "A chave Pix de destino é obrigatória")
    private String chaveDestino;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    private String mensagem;
}
