package com.Bank.bank_system.Repository;

import com.Bank.bank_system.Entity.ChavePix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChavePixRepository extends JpaRepository<ChavePix, Long> {

    Optional<ChavePix> findByValor(String valor);

    boolean existsByValor(String valor);

    List<ChavePix> findByContaId(Long contaId);

    List<ChavePix> findByContaClienteId(Long clienteId);
}
