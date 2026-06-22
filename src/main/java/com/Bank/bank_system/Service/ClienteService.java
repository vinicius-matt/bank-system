package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Exception.CamposIncorretosAtualizacaoException;
import com.Bank.bank_system.Exception.ClienteCadastradoException;
import com.Bank.bank_system.Exception.ClienteNaoEncontradoException;
import com.Bank.bank_system.Repository.ClienteRepository;
import com.Bank.bank_system.dto.ClienteDTO;
import com.Bank.bank_system.dto.ClienteResponseDTO;
import com.Bank.bank_system.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CurrentUser currentUser;

    public ClienteService(ClienteRepository clienteRepository, CurrentUser currentUser) {
        this.clienteRepository = clienteRepository;
        this.currentUser = currentUser;
    }

    private Cliente buscarClienteEntity(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));
    }

    private ClienteResponseDTO toDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCelular(),
                cliente.getCpf()
        );
    }

    public ClienteResponseDTO meuPerfil() {
        Long clienteId = currentUser.clienteId();
        if (clienteId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Seu login não possui perfil de cliente");
        }
        return toDTO(buscarClienteEntity(clienteId));
    }

    public Page<ClienteResponseDTO> listarClientesPaginado(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(this::toDTO);
    }

    public ClienteResponseDTO alterarMeuPerfil(ClienteResponseDTO dto) {
        Long clienteId = currentUser.clienteId();
        if (clienteId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Seu login não possui perfil de cliente");
        }
        return alterarCliente(clienteId, dto);
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

    public ClienteResponseDTO buscarCliente(Long id) {
        return toDTO(buscarClienteEntity(id));
    }

    public ClienteResponseDTO alterarCliente(Long id, ClienteResponseDTO clienteDTO) {
        Cliente cliente = buscarClienteEntity(id);

        if (clienteDTO.getNome() != null) {
            String nome = clienteDTO.getNome().trim();
            if (nome.isBlank() || "string".equalsIgnoreCase(nome)) {
                throw new CamposIncorretosAtualizacaoException("Nome inválido");
            }
            cliente.setNome(nome);
        }

        if (clienteDTO.getCelular() != null) {
            String celular = clienteDTO.getCelular().trim();
            if (!celular.matches("\\d{10,11}")) {
                throw new CamposIncorretosAtualizacaoException("Celular inválido");
            }
            cliente.setCelular(celular);
        }

        if (clienteDTO.getEmail() != null) {
            String email = clienteDTO.getEmail().trim();
            if (email.isBlank() || "string".equalsIgnoreCase(email)) {
                throw new CamposIncorretosAtualizacaoException("Email inválido");
            }
            if (clienteRepository.existsByEmail(email) && !cliente.getEmail().equals(email)) {
                throw new ClienteCadastradoException("Email já cadastrado");
            }
            cliente.setEmail(email);
        }

        return toDTO(clienteRepository.save(cliente));
    }
}
