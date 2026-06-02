package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Service.ClienteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController (ClienteService clienteService){
        this.clienteService = clienteService;
    }

    @PostMapping("/criar")
    public Cliente criarCliente(@RequestBody Cliente cliente){
        return clienteService.criarCliente(cliente);
    }

    @GetMapping("/listar")
    public List<Cliente> listarClientes(){
        return clienteService.listarClientes();
    }

    @GetMapping("/{id}")
    public Cliente buscarCliente(@PathVariable Long id){
        return clienteService.buscarCliente(id);
    }

}
