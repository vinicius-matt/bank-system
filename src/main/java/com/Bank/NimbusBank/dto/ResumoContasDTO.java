package com.Bank.NimbusBank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResumoContasDTO {
    private long total;
    private long ativas;
    private long bloqueadas;
    private long encerradas;
}
