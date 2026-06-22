package com.Bank.NimbusBank.Service;

import com.Bank.NimbusBank.Entity.Transacao;
import com.Bank.NimbusBank.Exception.ContaNaoEncontradaException;
import com.Bank.NimbusBank.Repository.TransacaoRepository;
import com.Bank.NimbusBank.dto.TransacaoResponseDTO;
import com.Bank.NimbusBank.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtratoServiceTest {

    @Mock
    private ContaService contaService;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private ExtratoService extratoService;

    @Test
    void deveRetornarExtratoComSucesso() {
        Transacao transacao = new Transacao();
        transacao.setId(1L);
        transacao.setTipo(TransactionType.DEPOSITO);
        transacao.setValor(BigDecimal.valueOf(100));
        transacao.setDescricao("Depósito");
        transacao.setData(LocalDateTime.now());

        when(transacaoRepository.findByContaIdOrderByDataDesc(1L))
                .thenReturn(List.of(transacao));

        List<TransacaoResponseDTO> resultado = extratoService.extrato(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(TransactionType.DEPOSITO, resultado.getFirst().getTipo());
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoExistirAoConsultarExtrato() {
        when(contaService.obterContaComAcesso(1L))
                .thenThrow(new ContaNaoEncontradaException("Conta não encontrada"));

        assertThrows(
                ContaNaoEncontradaException.class,
                () -> extratoService.extrato(1L)
        );
    }
}
