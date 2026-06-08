package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Exception.*;
import com.Bank.bank_system.Repository.ClienteRepository;
import com.Bank.bank_system.Repository.ContaRepository;
import com.Bank.bank_system.Repository.TransacaoRepository;
import com.Bank.bank_system.dto.ContaResponseDTO;
import com.Bank.bank_system.dto.SaldoResponseDTO;
import com.Bank.bank_system.dto.TransacaoResponseDTO;
import com.Bank.bank_system.model.StatusConta;
import com.Bank.bank_system.dto.ContaDTO;
import com.Bank.bank_system.model.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;
    private final ClienteRepository clienteRepository;

    public ContaService(ContaRepository contaRepository, TransacaoRepository transacaoRepository, ClienteRepository clienteRepository) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
        this.clienteRepository = clienteRepository;
    }

    //auxiliar
    private ContaResponseDTO salvarEConverter(Conta conta) {
        return toDTO(contaRepository.save(conta));
    }

    private void validarValor(BigDecimal valor) {

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
    }

    private void validarSaldoDisponivel(Conta conta, BigDecimal valor) {

        BigDecimal saldoDisponivel =
                conta.getSaldo().add(conta.getLimite());

        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo + limite insuficientes");
        }
    }

    private TransacaoResponseDTO toDTO(Transacao transacao) {

        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getTipo(),
                transacao.getValor(),
                transacao.getDescricao(),
                transacao.getData()
        );
    }

    private ContaResponseDTO toDTO(Conta conta) {
        return new ContaResponseDTO(
                conta.getId(),
                conta.getNumero(),
                conta.getSaldo(),
                conta.getLimite(),
                conta.getTipo(),
                conta.getStatus(),
                conta.getCliente().getId()
        );
    }

    private Conta buscarContaEntity(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() ->
                        new ContaNaoEncontradaException("Conta não encontrada"));
    }

    @Transactional
    public ContaResponseDTO criarConta(ContaDTO contaDTO) {

        Cliente cliente = clienteRepository.findById(contaDTO.getClienteId())
                .orElseThrow(() ->
                        new ClienteNaoEncontradoException("Cliente não encontrado"));

        Conta conta = new Conta();
        conta.setCliente(cliente);
        conta.setNumero(gerarConta());
        conta.setLimite(BigDecimal.ZERO);
        conta.setTipo(contaDTO.getTipoConta());
        conta.setSaldo(BigDecimal.ZERO);
        conta.setStatus(StatusConta.ATIVA);

        //Conta contaSalva = contaRepository.save(conta);

        return salvarEConverter(conta);
    }

    private String gerarConta() {
        Random random = new Random();
        String numero;

        do {
            numero = String.format("%08d", random.nextInt(100_000_000));
        } while (contaRepository.existsByNumero(numero));

        return numero;
    }

    private void registrarTransacao(Conta conta, TransactionType tipo, BigDecimal valor, String descricao) {

        Transacao transacao = new Transacao();
        transacao.setConta(conta);
        transacao.setTipo(tipo);
        transacao.setValor(valor);
        transacao.setDescricao(descricao);
        transacao.setData(LocalDateTime.now());

        transacaoRepository.save(transacao);
    }

    @Transactional
    public ContaResponseDTO sacar(Long contaId, BigDecimal valor) {

        Conta conta = buscarContaEntity(contaId);

        validarContaAtiva(conta);
        validarValor(valor);
        validarSaldoDisponivel(conta, valor);

        conta.setSaldo(conta.getSaldo().subtract(valor));

        registrarTransacao(
                conta,
                TransactionType.SAQUE,
                valor,
                "Saque realizado"
        );

        return salvarEConverter(conta);
    }

    @Transactional
    public ContaResponseDTO depositar(Long contaId, BigDecimal valor) {

        Conta conta = buscarContaEntity(contaId);

        validarContaAtiva(conta);
        validarValor(valor);

        conta.setSaldo(conta.getSaldo().add(valor));

        registrarTransacao(
                conta,
                TransactionType.DEPOSITO,
                valor,
                "Depósito realizado"
        );

        return salvarEConverter(conta);
    }

    @Transactional
    public void transferir(Long contaOrigemId,
                           Long contaDestinoId,
                           BigDecimal valor) {

        if (contaOrigemId.equals(contaDestinoId)) {
            throw new IllegalArgumentException(
                    "Não é possível transferir para a mesma conta");
        }

        Conta origem = buscarContaEntity(contaOrigemId);
        Conta destino = buscarContaEntity(contaDestinoId);

        validarContaAtiva(origem);
        validarContaAtiva(destino);

        validarValor(valor);
        validarSaldoDisponivel(origem, valor);

        origem.setSaldo(origem.getSaldo().subtract(valor));
        destino.setSaldo(destino.getSaldo().add(valor));

        registrarTransacao(
                origem,
                TransactionType.SAQUE,
                valor,
                "Transferência enviada"
        );

        registrarTransacao(
                destino,
                TransactionType.DEPOSITO,
                valor,
                "Transferência recebida"
        );

        contaRepository.save(origem);
        contaRepository.save(destino);
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> extrato(Long contaId) {

        buscarContaEntity(contaId);

        return transacaoRepository.findByContaId(contaId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private void validarContaAtiva(Conta conta) {
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new ContaBloqueadaException("A conta não está ativa para realizar operações");
        }
    }


    @Transactional
    public ContaResponseDTO bloquearConta(Long contaId) {

        Conta conta = buscarContaEntity(contaId);

        if (conta.getStatus() == StatusConta.BLOQUEADA) {
            throw new ContaBloqueadaException("Conta já está bloqueada");
        }
        if (conta.getStatus() == StatusConta.INATIVA) {
            throw new ContaBloqueadaException(
                    "Não é possível bloquear uma conta encerrada");
        }

        conta.setStatus(StatusConta.BLOQUEADA);

        return salvarEConverter(conta);
    }

    @Transactional
    public ContaResponseDTO ativarConta(Long id) {

        Conta conta = buscarContaEntity(id);

        if (conta.getStatus() == StatusConta.ATIVA) {
            throw new ContaJaAtivaException("Conta já está ativa");
        }

        if (conta.getStatus() == StatusConta.INATIVA) {
            throw new ContaBloqueadaException(
                    "Não é possível ativar uma conta encerrada");
        }

        conta.setStatus(StatusConta.ATIVA);

        return salvarEConverter(conta);
    }

    @Transactional(readOnly = true)
    public List<ContaResponseDTO> listarContas() {

        return contaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContaResponseDTO buscarConta(Long id) {

        Conta conta = buscarContaEntity(id);

        return toDTO(conta);
    }

    @Transactional(readOnly = true)
    public SaldoResponseDTO consultarSaldo(Long id) {

        Conta conta = buscarContaEntity(id);

        if (conta.getStatus() == StatusConta.BLOQUEADA ||
                conta.getStatus() == StatusConta.INATIVA) {

            throw new ContaBloqueadaException(
                    "Conta está bloqueada ou inativa");
        }

        return new SaldoResponseDTO(conta.getSaldo());
    }

    @Transactional
    public ContaResponseDTO encerrarConta(Long id) {
        Conta conta = buscarContaEntity(id);

        if (conta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            throw new EncerrarContaException("Não é possível encerrar uma conta com saldo disponível");
        }

        if (conta.getStatus() == StatusConta.INATIVA) {
            throw new EncerrarContaException("Esta conta ja foi encerrada");
        }

        conta.setStatus(StatusConta.INATIVA);

        return salvarEConverter(conta);
    }

    public ContaResponseDTO alterarLimite(Long id, BigDecimal valor) {
        Conta conta = buscarContaEntity(id);

        if (conta.getStatus() == StatusConta.BLOQUEADA ||  conta.getStatus() == StatusConta.INATIVA) {
            throw new ContaBloqueadaException("Não é possivel alterar o limite de uma conta Inativa ou Bloqueada");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O limite deve ser maior que zero");
        }

        conta.setLimite(conta.getLimite().add(valor));

        return salvarEConverter(conta);
    }

}