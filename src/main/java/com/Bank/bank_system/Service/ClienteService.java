package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Exception.ClienteCadastradoException;
import com.Bank.bank_system.Exception.ClienteNaoEncontradoException;
import com.Bank.bank_system.Repository.ClienteRepository;
import com.Bank.bank_system.dto.ClienteDTO;
import com.Bank.bank_system.dto.ClienteResponseDTO;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    //Auxiliar
    private Cliente buscarClienteEntity(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ClienteNaoEncontradoException("Cliente não encontrado"));
    }

    private ClienteResponseDTO toDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getNome(),
                cliente.getEmail()
        );
    }

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

    public List<ClienteResponseDTO> listarClientes() {

        return clienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ClienteResponseDTO buscarCliente(Long id) {
        return toDTO(buscarClienteEntity(id));
    }

    public ClienteResponseDTO alterarCliente(Long id, ClienteDTO clienteDTO) {

        Cliente cliente = buscarClienteEntity(id);

        //Refatorar depois de funcionar
        if (clienteDTO.getNome() != null) {
            clienteDTO.setNome(clienteDTO.getNome());
        }

        if (clienteDTO.getCpf() != null) {
            clienteDTO.setCpf(clienteDTO.getCpf());
        }

        if (clienteDTO.getEmail() != null) {
            clienteDTO.setEmail(clienteDTO.getEmail());
        }

        return toDTO(clienteRepository.save(cliente));
    }
}
