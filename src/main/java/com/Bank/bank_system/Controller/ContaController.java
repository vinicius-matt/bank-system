package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Service.ContaService;
import com.Bank.bank_system.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conta")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping("/criar")
    public ContaResponseDTO criarConta(@RequestBody ContaDTO contaDTO) {
        return contaService.criarConta(contaDTO);
    }

    @PostMapping("/{id}/depositar")
    public ContaResponseDTO depositar(@PathVariable Long id, @Valid @RequestBody ValorDTO dto) {
        return contaService.depositar(id, dto.getValor());
    }

    @PostMapping("/{id}/sacar")
    public ContaResponseDTO sacar(@PathVariable Long id, @Valid @RequestBody ValorDTO dto) {
        return contaService.sacar(id, dto.getValor());
    }

    @PostMapping("/transferir")
    public TransferenciaResponseDTO transferir(@Valid @RequestBody TransferenciaDTO dto) {
        return contaService.transferir(dto.getOrigemId(), dto.getDestinoId(), dto.getValor(), dto.getMensagem());
    }

    @PutMapping("/{id}/Bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    public ContaResponseDTO bloquear(@PathVariable Long id) {
        return contaService.bloquearConta(id);
    }

    @PutMapping("/{id}/Ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ContaResponseDTO ativar(@PathVariable Long id) {
        return contaService.ativarConta(id);
    }

    @GetMapping("/listar")
    public Page<ContaResponseDTO> listarContas(Pageable pageable) {
        return contaService.listarContas(pageable);
    }

    @GetMapping("/resumo")
    public ResumoContasDTO resumo() {
        return contaService.resumoStatus();
    }

    @GetMapping("/indisponiveis")
    public List<ContaResponseDTO> indisponiveis() {
        return contaService.listarIndisponiveis();
    }

    @GetMapping("/{id}")
    public ContaResponseDTO listar(@PathVariable Long id) {
        return contaService.buscarConta(id);
    }

    @GetMapping("/{id}/saldo")
    public SaldoResponseDTO consultarSaldo(@PathVariable Long id) {
        return contaService.consultarSaldo(id);
    }

    @PutMapping("/{id}/encerrar")
    @PreAuthorize("hasRole('ADMIN')")
    public ContaResponseDTO encerrarConta(@PathVariable Long id) {
        return contaService.encerrarConta(id);
    }

    @PutMapping("/{id}/alterarLimite")
    public ContaResponseDTO aumentarlimite(@PathVariable Long id, @Valid @RequestBody ValorDTO dto) {
        return contaService.aumentarlimite(id, dto.getValor());
    }
}
