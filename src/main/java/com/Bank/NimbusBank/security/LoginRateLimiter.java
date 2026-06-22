package com.Bank.NimbusBank.security;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_TENTATIVAS = 5;
    private static final Duration JANELA = Duration.ofMinutes(15);

    private record Registro(int tentativas, Instant ate) {}

    private final Map<String, Registro> mapa = new ConcurrentHashMap<>();

    public void verificarBloqueio(String chave) {
        Registro r = mapa.get(chave);
        if (r != null && r.tentativas() >= MAX_TENTATIVAS && r.ate().isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Muitas tentativas. Tente novamente em alguns minutos.");
        }
    }

    public void registrarFalha(String chave) {
        mapa.compute(chave, (k, r) -> {
            int t = (r == null || r.ate().isBefore(Instant.now())) ? 1 : r.tentativas() + 1;
            return new Registro(t, Instant.now().plus(JANELA));
        });
    }

    public void registrarSucesso(String chave) {
        mapa.remove(chave);
    }
}
