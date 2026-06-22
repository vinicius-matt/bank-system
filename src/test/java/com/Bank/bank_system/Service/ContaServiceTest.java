package com.Bank.bank_system.Service;


import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Exception.*;
import com.Bank.bank_system.Repository.ClienteRepository;
import com.Bank.bank_system.Repository.ContaRepository;
import com.Bank.bank_system.Repository.TransacaoRepository;
import com.Bank.bank_system.dto.ContaDTO;
import com.Bank.bank_system.dto.ContaResponseDTO;
import com.Bank.bank_system.dto.SaldoResponseDTO;
import com.Bank.bank_system.dto.TransacaoResponseDTO;
import com.Bank.bank_system.model.StatusConta;
import com.Bank.bank_system.model.TipoConta;
import com.Bank.bank_system.model.TransactionType;
import com.Bank.bank_system.security.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private com.Bank.bank_system.Repository.ChavePixRepository chavePixRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private ContaService contaService;

    @BeforeEach
    void setUp() {
        // Tratamos os testes como ADMIN: a checagem de dono (validarAcesso) é ignorada.
        lenient().when(currentUser.isAdmin()).thenReturn(true);
    }

    //auxiliar
    private Conta criarContaPadrao() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setCliente(cliente);
        conta.setNumero("12345678");
        conta.setSaldo(BigDecimal.valueOf(1000));
        conta.setLimite(BigDecimal.ZERO);
        conta.setTipo(TipoConta.CORRENTE);
        conta.setStatus(StatusConta.ATIVA);

        return conta;
    }

    @Test
    void deveCriarContaComSucesso() {

        //Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Jhairo");
        cliente.setCpf("12345678905");
        cliente.setCelular("47998028933");
        cliente.setEmail("jhairo@gmail.com");

        ContaDTO contaDTO = new ContaDTO();
        contaDTO.setClienteId(1L);
        contaDTO.setTipoConta(TipoConta.CORRENTE);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        when(contaRepository.existsByNumero(anyString()))
                .thenReturn(false);

        when(contaRepository.save(any(Conta.class))).thenAnswer(i -> i.getArgument(0));

        //Act

        ContaResponseDTO resultado = contaService.criarConta(contaDTO);

        //Assert

        assertNotNull(resultado);

        assertEquals(
                StatusConta.ATIVA,
                resultado.getStatus()
        );

        assertEquals(
                BigDecimal.ZERO,
                resultado.getSaldo()
        );

        assertEquals(
                BigDecimal.ZERO,
                resultado.getLimite()
        );

        assertEquals(
                TipoConta.CORRENTE,
                resultado.getTipo()
        );

        assertEquals(
                cliente.getId(),
                resultado.getClienteId()
        );
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {

        //Arrange
        ContaDTO contaDTO = new ContaDTO();
        contaDTO.setClienteId(1L);
        contaDTO.setTipoConta(TipoConta.CORRENTE);

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(
                ClienteNaoEncontradoException.class,
                () -> contaService.criarConta(contaDTO)
        );

    }

    @Test
    void deveDepositarComSucesso() {

        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setCliente(cliente);
        conta.setSaldo(BigDecimal.valueOf(100));
        conta.setLimite(BigDecimal.ZERO);
        conta.setStatus(StatusConta.ATIVA);
        conta.setTipo(TipoConta.CORRENTE);
        conta.setNumero("12345678");

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(contaRepository.save(any(Conta.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Act
        ContaResponseDTO resultado =
                contaService.depositar(1L, BigDecimal.valueOf(50));

        // Assert
        assertEquals(
                BigDecimal.valueOf(150),
                resultado.getSaldo()
        );

        verify(contaRepository).save(conta);
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoEncontrada() {

        // Arrange
        when(contaRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(
                ContaNaoEncontradaException.class,
                () -> contaService.depositar(
                        1L,
                        BigDecimal.valueOf(50)
                )
        );
    }

    @Test
    void deveLancarExcecaoAoDepositarValorInvalido() {

        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setCliente(cliente);
        conta.setSaldo(BigDecimal.valueOf(100));
        conta.setStatus(StatusConta.ATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> contaService.depositar(
                        1L,
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void deveSacarComSucesso() {

        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setCliente(cliente);
        conta.setSaldo(BigDecimal.valueOf(1000));
        conta.setLimite(BigDecimal.ZERO);
        conta.setStatus(StatusConta.ATIVA);
        conta.setTipo(TipoConta.CORRENTE);
        conta.setNumero("12345678");

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(contaRepository.save(any(Conta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ContaResponseDTO resultado =
                contaService.sacar(1L, BigDecimal.valueOf(200));

        // Assert
        assertNotNull(resultado);

        assertEquals(
                BigDecimal.valueOf(800),
                resultado.getSaldo()
        );

        verify(contaRepository).save(any(Conta.class));

        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void deveLancarExcecaoQuandoSaldoInsuficiente() {

        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setCliente(cliente);
        conta.setSaldo(BigDecimal.valueOf(100));
        conta.setLimite(BigDecimal.ZERO);
        conta.setStatus(StatusConta.ATIVA);
        conta.setTipo(TipoConta.CORRENTE);
        conta.setNumero("12345678");

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        // Act + Assert
        assertThrows(
                SaldoInsuficienteException.class,
                () -> contaService.sacar(
                        1L,
                        BigDecimal.valueOf(200)
                )
        );
    }

    @Test
    void deveTransferirComSucesso() {

        // Arrange

        Cliente clienteOrigem = new Cliente();
        clienteOrigem.setId(1L);

        Cliente clienteDestino = new Cliente();
        clienteDestino.setId(2L);

        Conta origem = new Conta();
        origem.setId(1L);
        origem.setCliente(clienteOrigem);
        origem.setSaldo(BigDecimal.valueOf(1000));
        origem.setLimite(BigDecimal.ZERO);
        origem.setStatus(StatusConta.ATIVA);

        Conta destino = new Conta();
        destino.setId(2L);
        destino.setCliente(clienteDestino);
        destino.setSaldo(BigDecimal.valueOf(500));
        destino.setLimite(BigDecimal.ZERO);
        destino.setStatus(StatusConta.ATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(origem));

        when(contaRepository.findById(2L))
                .thenReturn(Optional.of(destino));

        // Act

        contaService.transferir(
                1L,
                2L,
                BigDecimal.valueOf(200)
        );

        // Assert

        assertEquals(
                BigDecimal.valueOf(800),
                origem.getSaldo()
        );

        assertEquals(
                BigDecimal.valueOf(700),
                destino.getSaldo()
        );

        verify(contaRepository).save(origem);
        verify(contaRepository).save(destino);

        verify(transacaoRepository, times(2))
                .save(any(Transacao.class));
    }

    @Test
    void deveLancarExcecaoAoTransferirParaMesmaConta() {

        //Arrange
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> contaService.transferir(
                                1L,
                                1L,
                                BigDecimal.valueOf(100)
                        )
                );

        //Act + assert
        assertEquals(
                "Não é possível transferir para a mesma conta",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoSaldoInsuficienteNaTransferencia() {

        //Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Conta origem = new Conta();
        origem.setId(1L);
        origem.setCliente(cliente);
        origem.setSaldo(BigDecimal.valueOf(100));
        origem.setLimite(BigDecimal.ZERO);
        origem.setStatus(StatusConta.ATIVA);

        Conta destino = new Conta();
        destino.setId(2L);
        destino.setStatus(StatusConta.ATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(origem));

        when(contaRepository.findById(2L))
                .thenReturn(Optional.of(destino));

        //Act and assert
        assertThrows(
                SaldoInsuficienteException.class,
                () -> contaService.transferir(
                        1L,
                        2L,
                        BigDecimal.valueOf(200)
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaOrigemNaoEncontrada() {

        when(contaRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ContaNaoEncontradaException.class,
                () -> contaService.transferir(
                        1L,
                        2L,
                        BigDecimal.valueOf(100)
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaDestinoNaoEncontrada() {

        Conta origem = new Conta();
        origem.setId(1L);
        origem.setStatus(StatusConta.ATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(origem));

        when(contaRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                ContaNaoEncontradaException.class,
                () -> contaService.transferir(
                        1L,
                        2L,
                        BigDecimal.valueOf(100)
                )
        );
    }

    @Test
    void deveBloquearContaComSucesso() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setCliente(cliente);
        conta.setStatus(StatusConta.ATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(contaRepository.save(any(Conta.class)))
                .thenAnswer(i -> i.getArgument(0));

        ContaResponseDTO resultado =
                contaService.bloquearConta(1L);

        assertEquals(
                StatusConta.BLOQUEADA,
                resultado.getStatus()
        );
    }

    @Test
    void deveAtivarContaComSucesso() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setCliente(cliente);
        conta.setStatus(StatusConta.BLOQUEADA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(contaRepository.save(any(Conta.class)))
                .thenAnswer(i -> i.getArgument(0));

        ContaResponseDTO resultado =
                contaService.ativarConta(1L);

        assertEquals(
                StatusConta.ATIVA,
                resultado.getStatus()
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaJaEstiverAtiva() {

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setStatus(StatusConta.ATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        assertThrows(
                ContaJaAtivaException.class,
                () -> contaService.ativarConta(1L)
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaEstiverEncerradaAoAtivar() {

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setStatus(StatusConta.INATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        assertThrows(
                RegraDeNegocioException.class,
                () -> contaService.ativarConta(1L)
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaJaEstiverBloqueada() {

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setStatus(StatusConta.BLOQUEADA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        assertThrows(
                ContaBloqueadaException.class,
                () -> contaService.bloquearConta(1L)
        );
    }

    @Test
    void deveAlterarLimiteComSucesso() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setCliente(cliente);
        conta.setLimite(BigDecimal.valueOf(500));
        conta.setStatus(StatusConta.ATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(contaRepository.save(any(Conta.class)))
                .thenAnswer(i -> i.getArgument(0));

        ContaResponseDTO resultado =
                contaService.aumentarlimite(
                        1L,
                        BigDecimal.valueOf(200)
                );

        assertEquals(
                BigDecimal.valueOf(700),
                resultado.getLimite()
        );
    }
    @Test
    void deveLancarExcecaoQuandoValorLimiteForInvalido() {

        Conta conta = criarContaPadrao();

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        assertThrows(
                IllegalArgumentException.class,
                () -> contaService.aumentarlimite(
                        1L,
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaBloqueadaAoAlterarLimite() {

        Conta conta = criarContaPadrao();
        conta.setStatus(StatusConta.BLOQUEADA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        assertThrows(
                ContaBloqueadaException.class,
                () -> contaService.aumentarlimite(
                        1L,
                        BigDecimal.valueOf(100)
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaInativaAoAlterarLimite() {

        Conta conta = criarContaPadrao();
        conta.setStatus(StatusConta.INATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        assertThrows(
                ContaBloqueadaException.class,
                () -> contaService.aumentarlimite(
                        1L,
                        BigDecimal.valueOf(100)
                )
        );
    }

    @Test
    void deveEncerrarContaComSucesso() {

        Conta conta = criarContaPadrao();
        conta.setSaldo(BigDecimal.ZERO);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(contaRepository.save(any(Conta.class)))
                .thenAnswer(i -> i.getArgument(0));

        ContaResponseDTO resultado =
                contaService.encerrarConta(1L);

        assertEquals(
                StatusConta.INATIVA,
                resultado.getStatus()
        );

        verify(contaRepository).save(conta);
    }

    @Test
    void deveLancarExcecaoQuandoContaPossuirSaldo() {

        Conta conta = criarContaPadrao();
        conta.setSaldo(BigDecimal.valueOf(500));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        assertThrows(
                EncerrarContaException.class,
                () -> contaService.encerrarConta(1L)
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaJaEstiverEncerrada() {

        Conta conta = criarContaPadrao();
        conta.setSaldo(BigDecimal.ZERO);
        conta.setStatus(StatusConta.INATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        assertThrows(
                EncerrarContaException.class,
                () -> contaService.encerrarConta(1L)
        );
    }

    @Test
    void deveConsultarSaldoComSucesso() {

        Conta conta = criarContaPadrao();
        conta.setSaldo(BigDecimal.valueOf(1500));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        SaldoResponseDTO resultado =
                contaService.consultarSaldo(1L);

        assertNotNull(resultado);

        assertEquals(
                BigDecimal.valueOf(1500),
                resultado.getSaldo()
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaBloqueadaAoConsultarSaldo() {

        Conta conta = criarContaPadrao();
        conta.setStatus(StatusConta.BLOQUEADA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        assertThrows(
                ContaBloqueadaException.class,
                () -> contaService.consultarSaldo(1L)
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaInativaAoConsultarSaldo() {

        Conta conta = criarContaPadrao();
        conta.setStatus(StatusConta.INATIVA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        assertThrows(
                ContaBloqueadaException.class,
                () -> contaService.consultarSaldo(1L)
        );
    }

}
