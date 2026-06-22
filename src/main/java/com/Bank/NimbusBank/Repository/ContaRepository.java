package com.Bank.NimbusBank.Repository;

import com.Bank.NimbusBank.Entity.Conta;
import com.Bank.NimbusBank.model.StatusConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    boolean existsByNumero(String numero);

    Page<Conta> findByClienteId(Long clienteId, Pageable pageable);

    List<Conta> findByClienteId(Long clienteId);

    Page<Conta> findByClienteIdAndStatus(Long clienteId, StatusConta status, Pageable pageable);

    List<Conta> findByClienteIdAndStatusNot(Long clienteId, StatusConta status);
}
