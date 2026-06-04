package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Repository.ClienteRepository;
import com.Bank.bank_system.Repository.ContaRepository;
import com.Bank.bank_system.Service.ContaService;
import com.Bank.bank_system.dto.ContaDTO;
import com.Bank.bank_system.dto.TransferenciaDTO;
import com.Bank.bank_system.model.StatusConta;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/conta")
public class ContaController {

    private final ContaService contaService;
    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;

    public ContaController(ContaService contaService, ClienteRepository clienteRepository, ContaRepository contaRepository) {
        this.contaService = contaService;
        this.clienteRepository = clienteRepository;
        this.contaRepository = contaRepository;
    }
    @PostMapping("/criar")
    public Conta criarConta(@RequestBody ContaDTO contaDTO) {

        Cliente cliente = clienteRepository.findById(contaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado ou não existe"));

        Conta conta = new Conta();
        conta.setNumero(contaDTO.getNumero());
        conta.setCliente(cliente);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setTipo(contaDTO.getTipoConta());
        conta.setStatus(StatusConta.ATIVA);

        return contaRepository.save(conta);

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
    public List<Conta> listarContas(){
        return contaService.listarContas();
    }

    @GetMapping("/{id}")
    public Conta listar(@PathVariable Long id){
        return contaService.buscarConta(id);
    }

    @GetMapping("/{id}/extrato")
    public List<Transacao> extrato(@PathVariable Long id) {
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

}


