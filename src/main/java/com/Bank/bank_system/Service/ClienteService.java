package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Exception.ClienteCadastradoException;
import com.Bank.bank_system.Exception.ClienteNaoEncontradoException;
import com.Bank.bank_system.Repository.ClienteRepository;
import com.Bank.bank_system.dto.ClienteDTO;
import com.Bank.bank_system.dto.ClienteResponseDTO;
import com.Bank.bank_system.dto.ClienteUpdateDTO;
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

    public ClienteResponseDTO alterarCliente(Long id, ClienteUpdateDTO clienteDTO) {

        Cliente cliente = buscarClienteEntity(id);

        if (clienteDTO.getNome() != null) {

            String nome = clienteDTO.getNome().trim();

            if (nome.isBlank() || "string".equalsIgnoreCase(nome)) {
                throw new IllegalArgumentException("Nome inválido");
            }

            cliente.setNome(nome);
        }

        if (clienteDTO.getCelular() != null) {

            String celular = clienteDTO.getCelular().trim();

            if (!celular.matches("\\d+")) {
                throw new IllegalArgumentException("Celular deve conter apenas números");
            }

            cliente.setCelular(celular);
        }

        if (clienteDTO.getEmail() != null) {
            cliente.setEmail(clienteDTO.getEmail());
        }

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return toDTO(clienteAtualizado);
    }
}
