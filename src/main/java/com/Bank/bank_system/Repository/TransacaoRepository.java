package com.Bank.bank_system.Repository;

import com.Bank.bank_system.Entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
}
