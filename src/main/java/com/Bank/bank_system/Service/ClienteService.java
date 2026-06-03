package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Exception.ClienteCadastradoException;
import com.Bank.bank_system.Exception.ClienteNaoEncontradoException;
import com.Bank.bank_system.Repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente criarCliente(Cliente cliente) {

        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new ClienteCadastradoException("CPF já cadastrado");
        }

        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new ClienteCadastradoException("Email ja cadastrado");
        }
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente buscarCliente(Long id){
        return clienteRepository.findById(id).orElseThrow(() -> new ClienteNaoEncontradoException("Cliente nao encontrado"));
    }
}
