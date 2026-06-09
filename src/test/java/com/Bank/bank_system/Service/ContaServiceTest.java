package com.Bank.bank_system.Service;


import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Entity.Conta;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
}
