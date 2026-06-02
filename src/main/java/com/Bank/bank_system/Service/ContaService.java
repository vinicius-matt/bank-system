package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Repository.ContaRepository;
import com.Bank.bank_system.Repository.TransacaoRepository;
import com.Bank.bank_system.model.StatusConta;
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

        validarContaAtiva(conta);

        BigDecimal saldoDisponivel = conta.getSaldo()
                .add(conta.getLimite());

        //Validando se é um valor valido para transferencia
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        //Validando limite + saldo na transferencia
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new RuntimeException("Saldo + Limite insuficientes ");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));

        registrarTransacao(conta, TransactionType.SAQUE, valor, "Saque realizado");

        return contaRepository.save(conta);

    }

    @Transactional
    public void depositar(Long contaId, BigDecimal valor) {

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        validarContaAtiva(conta);

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

        validarContaAtiva(origem);
        validarContaAtiva(destino);

        // valida valor da transferência
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        // Saldo disponível = saldo + limite
        BigDecimal saldoDisponivel = origem.getSaldo()
                .add(origem.getLimite());

        // Validando saldo + limite
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new RuntimeException("Saldo + limite insuficientes");
        }

        // Debita da conta origem
        origem.setSaldo(origem.getSaldo().subtract(valor));

        registrarTransacao(
                origem,
                TransactionType.SAQUE,
                valor,
                "Transferência enviada"
        );

        // Deposita na conta destino
        destino.setSaldo(destino.getSaldo().add(valor));

        registrarTransacao(
                destino,
                TransactionType.DEPOSITO,
                valor,
                "Transferência recebida"
        );

        contaRepository.save(origem);
        contaRepository.save(destino);
    }

    public List<Transacao> extrato(Long contaId) {
        //vendo se existe
        contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        return transacaoRepository.findByContaId(contaId);
    }

    private void validarContaAtiva(Conta conta) {
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new RuntimeException("Conta não está ativa");
        }
    }
}

