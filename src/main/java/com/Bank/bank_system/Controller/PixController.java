package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Service.ChavePixService;
import com.Bank.bank_system.Service.ContaService;
import com.Bank.bank_system.dto.ChavePixDTO;
import com.Bank.bank_system.dto.ChavePixResponseDTO;
import com.Bank.bank_system.dto.TransferenciaPixDTO;
import com.Bank.bank_system.dto.TransferenciaResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pix")
public class PixController {

    private final ChavePixService chavePixService;
    private final ContaService contaService;

    public PixController(ChavePixService chavePixService, ContaService contaService) {
        this.chavePixService = chavePixService;
        this.contaService = contaService;
    }

    @PostMapping("/chaves")
    public ChavePixResponseDTO criarChave(@Valid @RequestBody ChavePixDTO dto) {
        return chavePixService.criar(dto);
    }

    @GetMapping("/chaves/minhas")
    public List<ChavePixResponseDTO> minhasChaves() {
        return chavePixService.minhasChaves();
    }

    @GetMapping("/chaves/conta/{contaId}")
    public List<ChavePixResponseDTO> chavesDaConta(@PathVariable Long contaId) {
        return chavePixService.listarPorConta(contaId);
    }

    @DeleteMapping("/chaves/{id}")
    public ResponseEntity<Void> excluirChave(@PathVariable Long id) {
        chavePixService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transferir")
    public TransferenciaResponseDTO transferir(@Valid @RequestBody TransferenciaPixDTO dto) {
        return contaService.transferirPix(dto.getOrigemId(), dto.getChaveDestino(), dto.getValor(), dto.getMensagem());
    }
}
