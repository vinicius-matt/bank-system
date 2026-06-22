package com.Bank.NimbusBank.Repository;

import com.Bank.NimbusBank.Entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByContaId(Long contaId);

    List<Transacao> findByContaIdOrderByDataDesc(Long contaId);
}
