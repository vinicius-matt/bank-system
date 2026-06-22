package com.Bank.NimbusBank.Service;

import com.Bank.NimbusBank.Entity.Cliente;
import com.Bank.NimbusBank.Entity.RefreshToken;
import com.Bank.NimbusBank.Entity.Usuario;
import com.Bank.NimbusBank.Repository.ClienteRepository;
import com.Bank.NimbusBank.Repository.UsuarioRepository;
import com.Bank.NimbusBank.auth.dto.AuthResponse;
import com.Bank.NimbusBank.auth.dto.LoginRequest;
import com.Bank.NimbusBank.auth.dto.RegisterRequest;
import com.Bank.NimbusBank.model.Role;
import com.Bank.NimbusBank.security.JwtService;
import com.Bank.NimbusBank.security.LoginRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final LoginRateLimiter rateLimiter;

    public AuthService(UsuarioRepository usuarioRepository,
                       ClienteRepository clienteRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService,
                       LoginRateLimiter rateLimiter) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.rateLimiter = rateLimiter;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com este email");
        }
        if (clienteRepository.existsByCpf(request.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setCpf(request.getCpf());
        cliente.setEmail(request.getEmail());
        cliente.setCelular(request.getCelular());

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(Role.USER)
                .cliente(cliente)
                .build();

        usuarioRepository.save(usuario);
        return buildResponse(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        String chave = request.getEmail() == null ? "" : request.getEmail().toLowerCase();
        rateLimiter.verificarBloqueio(chave);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));
        } catch (AuthenticationException ex) {
            rateLimiter.registrarFalha(chave);
            throw ex;
        }
        rateLimiter.registrarSucesso(chave);

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        return buildResponse(usuario);
    }

    public AuthResponse refresh(String refreshToken) {
        RefreshToken atual = refreshTokenService.validar(refreshToken);
        RefreshToken novo = refreshTokenService.rotacionar(atual);
        Usuario usuario = novo.getUsuario();
        String accessToken = jwtService.generateToken(usuario);
        return buildResponse(usuario, accessToken, novo.getToken());
    }

    public void logout(String refreshToken) {
        refreshTokenService.revogar(refreshToken);
    }

    private AuthResponse buildResponse(Usuario usuario) {
        String accessToken = jwtService.generateToken(usuario);
        RefreshToken refresh = refreshTokenService.criar(usuario);
        return buildResponse(usuario, accessToken, refresh.getToken());
    }

    private AuthResponse buildResponse(Usuario usuario, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .expiraEm(jwtService.getExpiration())
                .usuarioId(usuario.getId())
                .clienteId(usuario.getCliente() != null ? usuario.getCliente().getId() : null)
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole().name())
                .build();
    }
}
