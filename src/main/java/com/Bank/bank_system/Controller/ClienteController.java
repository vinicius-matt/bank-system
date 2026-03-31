package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Repository.ClienteRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController (ClienteRepository clienteRepository){
        this.clienteRepository = clienteRepository;
    }

    @PostMapping("/criar")
    public Cliente criarCliente(@RequestBody Cliente cliente){
        return clienteRepository.save(cliente);
    }
}
