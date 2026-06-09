package com.Bank.bank_system.Service;


import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Exception.ClienteNaoEncontradoException;
import com.Bank.bank_system.Exception.ContaNaoEncontradaException;
import com.Bank.bank_system.Exception.SaldoInsuficienteException;
import com.Bank.bank_system.Repository.ClienteRepository;
import com.Bank.bank_system.Repository.ContaRepository;
import com.Bank.bank_system.Repository.TransacaoRepository;
import com.Bank.bank_system.dto.ContaDTO;
import com.Bank.bank_system.dto.ContaResponseDTO;
import com.Bank.bank_system.model.StatusConta;
import com.Bank.bank_system.model.TipoConta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private ContaService contaService;

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
}
