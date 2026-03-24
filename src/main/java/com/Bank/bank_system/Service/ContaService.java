package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Repository.ContaRepository;
import com.Bank.bank_system.Repository.TransacaoRepository;
import com.Bank.bank_system.model.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaService(ContaRepository contaRepository, TransacaoRepository transacaoRepository) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    private void registrarTransacao(Conta conta, TransactionType tipo, BigDecimal valor, String descricao) {

        Transacao transacao = new Transacao();
        transacao.setConta(conta);
        transacao.setTipo(tipo);
        transacao.setValor(valor);
        transacao.setDescricao(descricao);

        transacaoRepository.save(transacao);
    }

    @Transactional
    public Conta sacar(Long contaId, BigDecimal valor) {

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));

        registrarTransacao(conta, TransactionType.SAQUE, valor, "Saque realizado");

        return contaRepository.save(conta);

    }

    @Transactional
    public void depositar(Long contaId, BigDecimal valor) {

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        conta.setSaldo(conta.getSaldo().add(valor));

        registrarTransacao(conta, TransactionType.DEPOSITO, valor, "Depósito realizado");
    }

    @Transactional
    public void transferir(Long contaOrigemId, Long contaDestinoId, BigDecimal valor) {

        Conta origem = contaRepository.findById(contaOrigemId)
                .orElseThrow(() -> new RuntimeException("Conta origem não encontrada"));

        Conta destino = contaRepository.findById(contaDestinoId)
                .orElseThrow(() -> new RuntimeException("Conta destino não encontrada"));

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        if (origem.getSaldo().compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        // debita da origem
        origem.setSaldo(origem.getSaldo().subtract(valor));
        registrarTransacao(origem, TransactionType.SAQUE, valor, "Transferência enviada");

        // adiciona na destino
        destino.setSaldo(destino.getSaldo().add(valor));
        registrarTransacao(destino, TransactionType.DEPOSITO, valor, "Transferência recebida");

        contaRepository.save(origem);
        contaRepository.save(destino);
    }

    public List<Transacao> extrato(Long contaId) {
        //Verificando a existencia
        contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        return transacaoRepository.findByContaId(contaId);
    }
}



