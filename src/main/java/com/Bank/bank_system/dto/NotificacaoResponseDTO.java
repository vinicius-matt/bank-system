package com.Bank.bank_system.dto;

import com.Bank.bank_system.model.TipoNotificacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NotificacaoResponseDTO {
    private Long id;
    private String titulo;
    private String mensagem;
    private TipoNotificacao tipo;
    private boolean lida;
    private LocalDateTime criadoEm;
}
