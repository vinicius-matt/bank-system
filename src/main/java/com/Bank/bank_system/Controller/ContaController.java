package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Service.ContaService;
import com.Bank.bank_system.dto.ContaDTO;
import com.Bank.bank_system.dto.ContaResponseDTO;
import com.Bank.bank_system.dto.TransacaoResponseDTO;
import com.Bank.bank_system.dto.TransferenciaDTO;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conta")
public class ContaController {

    private final ContaService contaService;
    public ContaController(ContaService contaService) {
        this.contaService = contaService;

    }

    @PostMapping("/criar")
    public Conta criarConta(@RequestBody ContaDTO contaDTO) {
        return  contaService.criarConta(contaDTO);
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

    @PutMapping("/{id}/Bloquear")
        public Conta bloquear(@PathVariable Long id){
        return contaService.bloquearConta(id);
    }

    @PutMapping("/{id}/Ativar")
    public Conta ativar(@PathVariable Long id){
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
    public Map<String, BigDecimal> consultarSaldo(@PathVariable Long id){
        return contaService.consultarSaldo(id);
    }

    @PutMapping("/{id}/encerrar")
    public void encerrar(@PathVariable Long id){
        contaService.encerrarConta(id);
    }

    @PutMapping("/{id}/alterarLimite")
        public Conta alterarLimite(@PathVariable Long id, BigDecimal valor){
       return contaService.alterarLimite(id, valor);
    }

}


