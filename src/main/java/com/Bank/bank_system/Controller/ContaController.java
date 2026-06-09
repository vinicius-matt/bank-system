package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Service.ContaService;
import com.Bank.bank_system.dto.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
        return  contaService.criarConta(contaDTO);
    }

    @PostMapping("/{id}/depositar")
    public ContaResponseDTO depositar(@PathVariable Long id, BigDecimal valor){
        return contaService.depositar(id, valor);
    }

    @PostMapping("/{id}/sacar")
    public ContaResponseDTO sacar(@PathVariable Long id, BigDecimal valor){
       return contaService.sacar(id, valor);
    }

    @PostMapping("/transferir")
    public void transferir(@RequestBody TransferenciaDTO dto){
        contaService.transferir(dto.getOrigemId(), dto.getDestinoId(), dto.getValor());
    }

    @PutMapping("/{id}/Bloquear")
        public ContaResponseDTO bloquear(@PathVariable Long id){
        return contaService.bloquearConta(id);
    }

    @PutMapping("/{id}/Ativar")
    public ContaResponseDTO ativar(@PathVariable Long id){
        return contaService.ativarConta(id);
    }

    @GetMapping("/listar")
    public List<ContaResponseDTO> listarContas(){
        return contaService.listarContas();
    }

    @GetMapping("/{id}")
    public ContaResponseDTO listar(@PathVariable Long id){
        return contaService.buscarConta(id);
    }

    @GetMapping("/{id}/extrato")
    public List<TransacaoResponseDTO> extrato(@PathVariable Long id) {
        return contaService.extrato(id);
    }

    @GetMapping("/{id}/saldo")
    public SaldoResponseDTO consultarSaldo(@PathVariable Long id){
        return contaService.consultarSaldo(id);
    }

    @PutMapping("/{id}/encerrar")
    public ContaResponseDTO encerrarConta(@PathVariable Long id){
       return contaService.encerrarConta(id);
    }

    @PutMapping("/{id}/alterarLimite")
        public ContaResponseDTO aumentarlimite(@PathVariable Long id, BigDecimal valor){
       return contaService.aumentarlimite(id, valor);
    }

}


