package com.Bank.NimbusBank.Service;

import com.Bank.NimbusBank.Entity.Notificacao;
import com.Bank.NimbusBank.Entity.Usuario;
import com.Bank.NimbusBank.Repository.NotificacaoRepository;
import com.Bank.NimbusBank.Repository.UsuarioRepository;
import com.Bank.NimbusBank.dto.NotificacaoResponseDTO;
import com.Bank.NimbusBank.model.TipoNotificacao;
import com.Bank.NimbusBank.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurrentUser currentUser;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              UsuarioRepository usuarioRepository,
                              CurrentUser currentUser) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.currentUser = currentUser;
    }

    public void criarParaUsuario(Usuario usuario, String titulo, String mensagem, TipoNotificacao tipo) {
        if (usuario == null) return;
        Notificacao n = Notificacao.builder()
                .usuario(usuario)
                .titulo(titulo)
                .mensagem(mensagem)
                .tipo(tipo)
                .lida(false)
                .build();
        notificacaoRepository.save(n);
    }

    public void criarParaCliente(Long clienteId, String titulo, String mensagem, TipoNotificacao tipo) {
        if (clienteId == null) return;
        usuarioRepository.findByClienteId(clienteId)
                .ifPresent(u -> criarParaUsuario(u, titulo, mensagem, tipo));
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> listar() {
        return notificacaoRepository.findByUsuarioIdOrderByCriadoEmDesc(currentUser.get().getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas() {
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(currentUser.get().getId());
    }

    @Transactional
    public void marcarComoLida(Long id) {
        Notificacao n = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada"));
        if (!n.getUsuario().getId().equals(currentUser.get().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        n.setLida(true);
        notificacaoRepository.save(n);
    }

    @Transactional
    public void marcarTodasComoLidas() {
        notificacaoRepository.marcarTodasComoLidas(currentUser.get().getId());
    }

    private NotificacaoResponseDTO toDTO(Notificacao n) {
        return new NotificacaoResponseDTO(
                n.getId(), n.getTitulo(), n.getMensagem(), n.getTipo(), n.isLida(), n.getCriadoEm());
    }
}
