package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.ChavePix;
import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Exception.*;
import com.Bank.bank_system.Repository.ChavePixRepository;
import com.Bank.bank_system.Repository.ClienteRepository;
import com.Bank.bank_system.Repository.ContaRepository;
import com.Bank.bank_system.Repository.TransacaoRepository;
import com.Bank.bank_system.dto.ContaDTO;
import com.Bank.bank_system.dto.ContaResponseDTO;
import com.Bank.bank_system.dto.SaldoResponseDTO;
import com.Bank.bank_system.dto.TransacaoResponseDTO;
import com.Bank.bank_system.dto.TransferenciaResponseDTO;
import com.Bank.bank_system.model.StatusConta;
import com.Bank.bank_system.model.TipoNotificacao;
import com.Bank.bank_system.model.TransactionType;
import com.Bank.bank_system.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;
    private final ClienteRepository clienteRepository;
    private final ChavePixRepository chavePixRepository;
    private final NotificacaoService notificacaoService;
    private final CurrentUser currentUser;

    public ContaService(ContaRepository contaRepository,
                        TransacaoRepository transacaoRepository,
                        ClienteRepository clienteRepository,
                        ChavePixRepository chavePixRepository,
                        NotificacaoService notificacaoService,
                        CurrentUser currentUser) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
        this.clienteRepository = clienteRepository;
        this.chavePixRepository = chavePixRepository;
        this.notificacaoService = notificacaoService;
        this.currentUser = currentUser;
    }

    // ===================== Autorização (dono / admin) =====================

    /** Garante que o usuário logado é dono da conta — ou é ADMIN. */
    private void validarAcesso(Conta conta) {
        if (currentUser.isAdmin()) return;
        Long meuClienteId = currentUser.clienteId();
        Long donoId = conta.getCliente() != null ? conta.getCliente().getId() : null;
        if (meuClienteId == null || !meuClienteId.equals(donoId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem acesso a esta conta");
        }
    }

    /** Resolve o cliente dono de uma nova conta: o próprio usuário, ou (ADMIN) o id informado. */
    private Cliente resolverClienteDestino(ContaDTO dto) {
        Long clienteId;
        if (currentUser.isAdmin() && dto.getClienteId() != null) {
            clienteId = dto.getClienteId();
        } else {
            clienteId = currentUser.clienteId();
        }
        if (clienteId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não foi possível identificar o cliente para esta conta");
        }
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));
    }

    // ===================== Helpers =====================

    private ContaResponseDTO salvarEConverter(Conta conta) {
        return toDTO(contaRepository.save(conta));
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
    }

    private void validarSaldoDisponivel(Conta conta, BigDecimal valor) {
        BigDecimal saldoDisponivel = conta.getSaldo().add(conta.getLimite());
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo + limite insuficientes");
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
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));
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

    private void validarContaAtiva(Conta conta) {
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new ContaBloqueadaException("A conta não está ativa para realizar operações");
        }
    }

    // Usado pelo serviço de exportação de extrato (com checagem de acesso)
    public Conta obterContaComAcesso(Long id) {
        Conta conta = buscarContaEntity(id);
        validarAcesso(conta);
        return conta;
    }

    public List<Transacao> transacoesDaConta(Long id) {
        Conta conta = obterContaComAcesso(id);
        return transacaoRepository.findByContaIdOrderByDataDesc(conta.getId());
    }

    // ===================== Operações =====================

    @Transactional
    public ContaResponseDTO criarConta(ContaDTO contaDTO) {
        Cliente cliente = resolverClienteDestino(contaDTO);

        Conta conta = new Conta();
        conta.setCliente(cliente);
        conta.setNumero(gerarConta());
        conta.setLimite(BigDecimal.ZERO);
        conta.setTipo(contaDTO.getTipoConta());
        conta.setSaldo(BigDecimal.ZERO);
        conta.setStatus(StatusConta.ATIVA);

        return salvarEConverter(conta);
    }

    @Transactional
    public ContaResponseDTO sacar(Long contaId, BigDecimal valor) {
        Conta conta = buscarContaEntity(contaId);
        validarAcesso(conta);
        validarContaAtiva(conta);
        validarValor(valor);
        validarSaldoDisponivel(conta, valor);

        conta.setSaldo(conta.getSaldo().subtract(valor));
        registrarTransacao(conta, TransactionType.SAQUE, valor, "Saque realizado");
        ContaResponseDTO dto = salvarEConverter(conta);
        notificarConta(conta, "Saque realizado",
                "Saque de " + formatar(valor) + " na conta " + conta.getNumero(), TipoNotificacao.TRANSACAO);
        return dto;
    }

    @Transactional
    public ContaResponseDTO depositar(Long contaId, BigDecimal valor) {
        Conta conta = buscarContaEntity(contaId);
        validarAcesso(conta);
        validarContaAtiva(conta);
        validarValor(valor);

        conta.setSaldo(conta.getSaldo().add(valor));
        registrarTransacao(conta, TransactionType.DEPOSITO, valor, "Depósito realizado");
        ContaResponseDTO dto = salvarEConverter(conta);
        notificarConta(conta, "Depósito recebido",
                "Depósito de " + formatar(valor) + " na conta " + conta.getNumero(), TipoNotificacao.TRANSACAO);
        return dto;
    }

    /** Sobrecarga sem mensagem (compatibilidade). */
    @Transactional
    public TransferenciaResponseDTO transferir(Long contaOrigemId, Long contaDestinoId, BigDecimal valor) {
        return transferir(contaOrigemId, contaDestinoId, valor, null);
    }

    @Transactional
    public TransferenciaResponseDTO transferir(Long contaOrigemId,
                                               Long contaDestinoId,
                                               BigDecimal valor,
                                               String mensagem) {
        if (contaOrigemId.equals(contaDestinoId)) {
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta");
        }
        Conta origem = buscarContaEntity(contaOrigemId);
        Conta destino = buscarContaEntity(contaDestinoId);
        // Só preciso ser dono da conta de ORIGEM (posso transferir para qualquer destino)
        validarAcesso(origem);
        return executarTransferencia(origem, destino, valor, mensagem, TransactionType.TRANSFERENCIA, "Transferência");
    }

    @Transactional
    public TransferenciaResponseDTO transferirPix(Long contaOrigemId,
                                                  String chaveDestino,
                                                  BigDecimal valor,
                                                  String mensagem) {
        ChavePix chave = chavePixRepository.findByValor(chaveDestino == null ? "" : chaveDestino.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chave Pix não encontrada"));

        Conta destino = chave.getConta();
        Conta origem = buscarContaEntity(contaOrigemId);

        if (origem.getId().equals(destino.getId())) {
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta");
        }
        validarAcesso(origem);
        return executarTransferencia(origem, destino, valor, mensagem, TransactionType.PIX, "Pix");
    }

    private TransferenciaResponseDTO executarTransferencia(Conta origem, Conta destino, BigDecimal valor,
                                                           String mensagem, TransactionType tipo, String rotulo) {
        validarContaAtiva(origem);
        validarContaAtiva(destino);
        validarValor(valor);
        validarSaldoDisponivel(origem, valor);

        origem.setSaldo(origem.getSaldo().subtract(valor));
        destino.setSaldo(destino.getSaldo().add(valor));

        String sufixo = (mensagem != null && !mensagem.isBlank()) ? ": " + mensagem.trim() : "";
        registrarTransacao(origem, tipo, valor, rotulo + " enviado" + sufixo);
        registrarTransacao(destino, tipo, valor, rotulo + " recebido" + sufixo);

        contaRepository.save(origem);
        contaRepository.save(destino);

        TipoNotificacao tn = tipo == TransactionType.PIX ? TipoNotificacao.PIX : TipoNotificacao.TRANSACAO;
        notificarConta(origem, rotulo + " enviado",
                "Você enviou " + formatar(valor) + " da conta " + origem.getNumero() + sufixo, tn);
        notificarConta(destino, rotulo + " recebido",
                "Você recebeu " + formatar(valor) + " na conta " + destino.getNumero() + sufixo, tn);

        return TransferenciaResponseDTO.builder()
                .origemId(origem.getId())
                .origemNumero(origem.getNumero())
                .saldoOrigem(origem.getSaldo())
                .destinoId(destino.getId())
                .destinoNumero(destino.getNumero())
                .valor(valor)
                .mensagem(mensagem)
                .data(LocalDateTime.now())
                .build();
    }

    private void notificarConta(Conta conta, String titulo, String mensagem, TipoNotificacao tipo) {
        Long clienteId = conta.getCliente() != null ? conta.getCliente().getId() : null;
        notificacaoService.criarParaCliente(clienteId, titulo, mensagem, tipo);
    }

    private String formatar(BigDecimal v) {
        return "R$ " + (v == null ? "0,00" : v.toString());
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> extrato(Long contaId) {
        Conta conta = buscarContaEntity(contaId);
        validarAcesso(conta);
        return transacaoRepository.findByContaIdOrderByDataDesc(contaId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public ContaResponseDTO bloquearConta(Long contaId) {
        Conta conta = buscarContaEntity(contaId);
        validarAcesso(conta);

        if (conta.getStatus() == StatusConta.BLOQUEADA) {
            throw new ContaBloqueadaException("Conta já está bloqueada");
        }
        if (conta.getStatus() == StatusConta.INATIVA) {
            throw new RegraDeNegocioException("Não é possível bloquear uma conta encerrada");
        }
        conta.setStatus(StatusConta.BLOQUEADA);
        return salvarEConverter(conta);
    }

    @Transactional
    public ContaResponseDTO ativarConta(Long id) {
        Conta conta = buscarContaEntity(id);
        validarAcesso(conta);

        if (conta.getStatus() == StatusConta.ATIVA) {
            throw new ContaJaAtivaException("Conta já está ativa");
        }
        if (conta.getStatus() == StatusConta.INATIVA) {
            throw new RegraDeNegocioException("Não é possível ativar uma conta encerrada");
        }
        conta.setStatus(StatusConta.ATIVA);
        return salvarEConverter(conta);
    }

    @Transactional(readOnly = true)
    public Page<ContaResponseDTO> listarContas(Pageable pageable) {
        Page<Conta> page = currentUser.isAdmin()
                ? contaRepository.findAll(pageable)
                : contaRepository.findByClienteId(requireClienteId(), pageable);
        return page.map(this::toDTO);
    }

    private Long requireClienteId() {
        Long id = currentUser.clienteId();
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Seu login não possui um perfil de cliente associado");
        }
        return id;
    }

    @Transactional(readOnly = true)
    public ContaResponseDTO buscarConta(Long id) {
        Conta conta = buscarContaEntity(id);
        validarAcesso(conta);
        return toDTO(conta);
    }

    @Transactional(readOnly = true)
    public SaldoResponseDTO consultarSaldo(Long id) {
        Conta conta = buscarContaEntity(id);
        validarAcesso(conta);

        if (conta.getStatus() == StatusConta.BLOQUEADA) {
            throw new RegraDeNegocioException("Conta bloqueada. Operação de consulta de saldo não permitida.");
        }
        if (conta.getStatus() == StatusConta.INATIVA) {
            throw new RegraDeNegocioException("Conta encerrada. Operação de consulta de saldo não permitida.");
        }
        return new SaldoResponseDTO(conta.getSaldo());
    }

    @Transactional
    public ContaResponseDTO encerrarConta(Long id) {
        Conta conta = buscarContaEntity(id);
        validarAcesso(conta);

        if (conta.getStatus() == StatusConta.INATIVA) {
            throw new EncerrarContaException("Esta conta ja foi encerrada");
        }
        if (conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new EncerrarContaException("A conta deve estar com saldo zerado para ser encerrada");
        }
        conta.setStatus(StatusConta.INATIVA);
        return salvarEConverter(conta);
    }

    @Transactional
    public ContaResponseDTO aumentarlimite(Long id, BigDecimal valor) {
        Conta conta = buscarContaEntity(id);
        validarAcesso(conta);

        if (conta.getStatus() == StatusConta.BLOQUEADA || conta.getStatus() == StatusConta.INATIVA) {
            throw new ContaBloqueadaException("Não é possivel alterar o limite de uma conta Inativa ou Bloqueada");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O limite deve ser maior que zero");
        }
        conta.setLimite(conta.getLimite().add(valor));
        return salvarEConverter(conta);
    }
}
