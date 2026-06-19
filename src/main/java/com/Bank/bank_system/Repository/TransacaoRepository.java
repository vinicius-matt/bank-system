package com.Bank.bank_system.Repository;

import com.Bank.bank_system.Entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByContaId(Long contaId);

    List<Transacao> findByContaIdOrderByDataDesc(Long contaId);
}
