package com.Bank.bank_system.Repository;

import com.Bank.bank_system.Entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Conta, Long> {
}
