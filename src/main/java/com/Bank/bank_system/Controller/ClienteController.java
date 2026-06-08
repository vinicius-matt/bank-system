package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Service.ClienteService;
import com.Bank.bank_system.dto.ClienteDTO;
import com.Bank.bank_system.dto.ClienteResponseDTO;
import jakarta.validation.Valid;
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
    public Cliente criarCliente(@RequestBody ClienteDTO clienteRequest){
        return clienteService.criarCliente(clienteRequest);
    }

    @GetMapping("/listar")
    public List<ClienteResponseDTO> listarClientes(){
        return clienteService.listarClientes();
    }

    @GetMapping("/{id}")
    public ClienteResponseDTO buscarCliente(@PathVariable Long id){
        return clienteService.buscarCliente(id);
    }

    @PatchMapping("/{id}")
    public ClienteResponseDTO atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteResponseDTO clienteRequest) {

        return clienteService.alterarCliente(id, clienteRequest);
    }

}
