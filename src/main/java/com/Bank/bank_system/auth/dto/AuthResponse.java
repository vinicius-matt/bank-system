package com.Bank.bank_system.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String tipo;
    private long expiraEm;
    private Long usuarioId;
    private Long clienteId;
    private String nome;
    private String email;
    private String role;
}
