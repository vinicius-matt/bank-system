package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Exception.ContaBloqueadaException;
import com.Bank.bank_system.Exception.ContaJaAtivaException;
import com.Bank.bank_system.Exception.ContaNaoEncontradaException;
import com.Bank.bank_system.Exception.SaldoInsuficienteException;
import com.Bank.bank_system.Repository.ContaRepository;
import com.Bank.bank_system.Repository.TransacaoRepository;
import com.Bank.bank_system.model.StatusConta;
import com.Bank.bank_system.model.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));

        validarContaAtiva(conta);

        BigDecimal saldoDisponivel = conta.getSaldo()
                .add(conta.getLimite());

        //Validando se é um valor valido para transferencia
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        //Validando limite + saldo na transferencia
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo + Limite insuficientes ");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));

        registrarTransacao(conta, TransactionType.SAQUE, valor, "Saque realizado");

        return contaRepository.save(conta);

    }

    @Transactional
    public void depositar(Long contaId, BigDecimal valor) {

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));

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
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta origem não encontrada"));

        Conta destino = contaRepository.findById(contaDestinoId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta destino não encontrada"));

        validarContaAtiva(origem);
        validarContaAtiva(destino);

        // valida valor da transferência
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SaldoInsuficienteException("Valor inválido");
        }

        // Saldo disponível = saldo + limite
        BigDecimal saldoDisponivel = origem.getSaldo()
                .add(origem.getLimite());

        // Validando saldo + limite
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo + limite insuficientes");
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
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));

        return transacaoRepository.findByContaId(contaId);
    }

    private void validarContaAtiva(Conta conta) {
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new ContaBloqueadaException("Conta não está ativa");
        }
    }

    @Transactional
    public Conta bloquearConta(Long contaId) {

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));

        if (conta.getStatus() == StatusConta.BLOQUEADA) {
            throw new ContaBloqueadaException("Conta já está bloqueada");
        }

        conta.setStatus(StatusConta.BLOQUEADA);

        return contaRepository.save(conta);
    }

    @Transactional
    public Conta ativarConta(Long contaId) {

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));

        if (conta.getStatus() == StatusConta.ATIVA) {
            throw new ContaJaAtivaException("Conta já está ativa");
        }

        conta.setStatus(StatusConta.ATIVA);

        return contaRepository.save(conta);
    }

    @Transactional
    public List<Conta> listarContas() {
        return contaRepository.findAll();
    }

    @Transactional
    public Conta buscarConta(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() ->
                        new ContaNaoEncontradaException("Conta não encontrada"));
    }

    @Transactional
    public Map<String, BigDecimal> consultarSaldo(Long id) {
        Conta conta = buscarConta(id);

        if (conta.getStatus() == StatusConta.BLOQUEADA || conta.getStatus() == StatusConta.INATIVA) {
            throw new ContaBloqueadaException("Conta esta bloqueada ou Inativa");
        }

        return Map.of("saldo", conta.getSaldo());
    }

    @Transactional
    public Conta encerrarConta(Long id) {
        Conta conta = buscarConta(id);

        if (conta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("A conta Possui saldo e não pode ser encerrada");
        }

        if (conta.getStatus() == StatusConta.INATIVA) {
            throw new ContaNaoEncontradaException("Esta conta ja foi encerrada ");
        }

        conta.setStatus(StatusConta.INATIVA);

        return contaRepository.save(conta);
    }

}

