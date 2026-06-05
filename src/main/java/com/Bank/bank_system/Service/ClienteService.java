package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Exception.ClienteCadastradoException;
import com.Bank.bank_system.Exception.ClienteNaoEncontradoException;
import com.Bank.bank_system.Repository.ClienteRepository;
import com.Bank.bank_system.dto.ClienteDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente criarCliente(ClienteDTO clienteRequest) {

        if (clienteRepository.existsByCpf(clienteRequest.getCpf())) {
            throw new ClienteCadastradoException("CPF já cadastrado");
        }

        if (clienteRepository.existsByEmail(clienteRequest.getEmail())) {
            throw new ClienteCadastradoException("Email já cadastrado");
        }

        Cliente cliente = new Cliente();

        cliente.setNome(clienteRequest.getNome());
        cliente.setCpf(clienteRequest.getCpf());
        cliente.setEmail(clienteRequest.getEmail());
        cliente.setCelular(clienteRequest.getCelular());

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {

        return clienteRepository.findAll();
    }

    public Cliente buscarCliente(Long id){
        return clienteRepository.findById(id).orElseThrow(() -> new ClienteNaoEncontradoException("Cliente nao encontrado"));
    }

    public Cliente alterarCliente(Long id, ClienteDTO clienteRequest) {


        Cliente clienteExistente = clienteRepository.findById(id).orElseThrow(
                () -> new ClienteNaoEncontradoException("Cliente não encontrado")
        );

        clienteExistente.setNome(clienteRequest.getNome());
        clienteExistente.setCpf(clienteRequest.getCpf());
        clienteExistente.setEmail(clienteRequest.getEmail());
        clienteExistente.setCelular(clienteRequest.getCelular());

        return clienteRepository.save(clienteExistente);
    }
}
