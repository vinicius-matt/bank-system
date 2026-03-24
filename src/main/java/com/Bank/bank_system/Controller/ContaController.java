package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Service.ContaService;
import com.Bank.bank_system.dto.TransferenciaDTO;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/{id}/extrato")
    public List<Transacao> extrato(@PathVariable Long id) {
        return contaService.extrato(id);
    }

    @PostMapping("/{id}/depositar")
        public void depositar(@PathVariable Long id, BigDecimal valor){
        contaService.depositar(id, valor);
    }

    @PostMapping("/{id}/sacar")
    public void sacar(@PathVariable Long id, BigDecimal valor){
        contaService.sacar(id, valor);
    }

    @PostMapping("/transferir")
    public void transferir(@RequestBody TransferenciaDTO dto){
        contaService.transferir(dto.getOrigemId(), dto.getDestinoId(), dto.getValor());
    }
}


