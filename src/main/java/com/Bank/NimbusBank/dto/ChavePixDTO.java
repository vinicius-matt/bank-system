package com.Bank.NimbusBank.dto;

import com.Bank.NimbusBank.model.TipoChavePix;
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

    private String valor;
}
