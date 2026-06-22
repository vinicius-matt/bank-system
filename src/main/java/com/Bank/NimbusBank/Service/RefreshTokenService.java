package com.Bank.NimbusBank.Service;

import com.Bank.NimbusBank.Entity.RefreshToken;
import com.Bank.NimbusBank.Entity.Usuario;
import com.Bank.NimbusBank.Repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public RefreshToken criar(Usuario usuario) {
        RefreshToken rt = RefreshToken.builder()
                .token(UUID.randomUUID().toString() + UUID.randomUUID())
                .usuario(usuario)
                .expiresAt(Instant.now().plusMillis(refreshExpiration))
                .revoked(false)
                .build();
        return repository.save(rt);
    }

    public RefreshToken validar(String token) {
        RefreshToken rt = repository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido"));
        if (!rt.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão expirada. Faça login novamente.");
        }
        return rt;
    }

    public RefreshToken rotacionar(RefreshToken atual) {
        atual.setRevoked(true);
        repository.save(atual);
        return criar(atual.getUsuario());
    }

    public void revogar(String token) {
        repository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            repository.save(rt);
        });
    }
}
