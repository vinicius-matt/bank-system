package com.Bank.NimbusBank.Repository;

import com.Bank.NimbusBank.Entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
