package com.Bank.NimbusBank.Repository;

import com.Bank.NimbusBank.Entity.RefreshToken;
import com.Bank.NimbusBank.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByUsuario(Usuario usuario);
}
