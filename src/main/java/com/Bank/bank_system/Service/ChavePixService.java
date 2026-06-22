package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.ChavePix;
import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Repository.ChavePixRepository;
import com.Bank.bank_system.dto.ChavePixDTO;
import com.Bank.bank_system.dto.ChavePixResponseDTO;
import com.Bank.bank_system.model.TipoChavePix;
import com.Bank.bank_system.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ChavePixService {

    private final ChavePixRepository chavePixRepository;
    private final ContaService contaService;
    private final CurrentUser currentUser;

    public ChavePixService(ChavePixRepository chavePixRepository,
                           ContaService contaService,
                           CurrentUser currentUser) {
        this.chavePixRepository = chavePixRepository;
        this.contaService = contaService;
        this.currentUser = currentUser;
    }

    @Transactional
    public ChavePixResponseDTO criar(ChavePixDTO dto) {
        Conta conta = contaService.obterContaComAcesso(dto.getContaId());

        String valor = normalizarValor(dto.getTipo(), dto.getValor());

        if (chavePixRepository.existsByValor(valor)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta chave Pix já está cadastrada");
        }

        ChavePix chave = ChavePix.builder()
                .tipo(dto.getTipo())
                .valor(valor)
                .conta(conta)
                .build();

        return toDTO(chavePixRepository.save(chave));
    }

    @Transactional(readOnly = true)
    public List<ChavePixResponseDTO> listarPorConta(Long contaId) {
        contaService.obterContaComAcesso(contaId);
        return chavePixRepository.findByContaId(contaId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ChavePixResponseDTO> minhasChaves() {
        Long clienteId = currentUser.clienteId();
        if (clienteId == null) return List.of();
        return chavePixRepository.findByContaClienteId(clienteId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public void excluir(Long id) {
        ChavePix chave = chavePixRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chave Pix não encontrada"));
        contaService.obterContaComAcesso(chave.getConta().getId());
        chavePixRepository.delete(chave);
    }

    private String normalizarValor(TipoChavePix tipo, String valor) {
        if (tipo == TipoChavePix.ALEATORIA) {
            return UUID.randomUUID().toString();
        }
        if (valor == null || valor.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o valor da chave");
        }
        String v = valor.trim();
        switch (tipo) {
            case EMAIL -> {
                v = v.toLowerCase();
                if (!v.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido para chave Pix");
                }
            }
            case CPF -> {
                v = v.replaceAll("\\D", "");
                if (v.length() != 11) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF deve conter 11 dígitos");
                }
            }
            case CELULAR -> {
                v = v.replaceAll("\\D", "");
                if (v.length() < 10 || v.length() > 11) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Celular inválido");
                }
                v = "+55" + v;
            }
            default -> { }
        }
        return v;
    }

    private ChavePixResponseDTO toDTO(ChavePix c) {
        return new ChavePixResponseDTO(
                c.getId(), c.getTipo(), c.getValor(), c.getConta().getId(), c.getConta().getNumero());
    }
}
