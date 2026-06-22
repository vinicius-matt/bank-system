package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Service.ClienteService;
import com.Bank.bank_system.dto.ClienteDTO;
import com.Bank.bank_system.dto.ClienteResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/me")
    public ClienteResponseDTO meuPerfil() {
        return clienteService.meuPerfil();
    }

    @PatchMapping("/me")
    public ClienteResponseDTO atualizarMeuPerfil(@Valid @RequestBody ClienteResponseDTO clienteRequest) {
        return clienteService.alterarMeuPerfil(clienteRequest);
    }

    @PostMapping("/criar")
    @PreAuthorize("hasRole('ADMIN')")
    public Cliente criarCliente(@RequestBody ClienteDTO clienteRequest) {
        return clienteService.criarCliente(clienteRequest);
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ClienteResponseDTO> listarClientes(Pageable pageable) {
        return clienteService.listarClientesPaginado(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClienteResponseDTO buscarCliente(@PathVariable Long id) {
        return clienteService.buscarCliente(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClienteResponseDTO atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteResponseDTO clienteRequest) {
        return clienteService.alterarCliente(id, clienteRequest);
    }
}
