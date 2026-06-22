package com.Bank.NimbusBank.Service;

import com.Bank.NimbusBank.Entity.Transacao;
import com.Bank.NimbusBank.Repository.TransacaoRepository;
import com.Bank.NimbusBank.dto.TransacaoResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExtratoService {

    private final ContaService contaService;
    private final TransacaoRepository transacaoRepository;

    public ExtratoService(ContaService contaService, TransacaoRepository transacaoRepository) {
        this.contaService = contaService;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> extrato(Long contaId) {
        contaService.obterContaComAcesso(contaId);
        return transacaoRepository.findByContaIdOrderByDataDesc(contaId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private TransacaoResponseDTO toDTO(Transacao t) {
        return new TransacaoResponseDTO(t.getId(), t.getTipo(), t.getValor(), t.getDescricao(), t.getData());
    }
}
