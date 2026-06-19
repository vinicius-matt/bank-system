package com.Bank.bank_system.dto;

import com.Bank.bank_system.model.TipoChavePix;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChavePixDTO {

    @NotNull(message = "O tipo da chave é obrigatório")
    private TipoChavePix tipo;

    @NotNull(message = "A conta é obrigatória")
    private Long contaId;

    /** Valor da chave. Ignorado quando o tipo é ALEATORIA (gerada pelo sistema). */
    private String valor;
}
