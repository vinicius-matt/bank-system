package com.Bank.bank_system.Controller;

import com.Bank.bank_system.Entity.Usuario;
import com.Bank.bank_system.Service.AuthService;
import com.Bank.bank_system.auth.dto.AuthResponse;
import com.Bank.bank_system.auth.dto.LoginRequest;
import com.Bank.bank_system.auth.dto.RefreshRequest;
import com.Bank.bank_system.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) RefreshRequest request) {
        if (request != null && request.getRefreshToken() != null) {
            authService.logout(request.getRefreshToken());
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal Usuario usuario) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", usuario.getId());
        body.put("nome", usuario.getNome());
        body.put("email", usuario.getEmail());
        body.put("role", usuario.getRole().name());
        body.put("clienteId", usuario.getCliente() != null ? usuario.getCliente().getId() : null);
        return ResponseEntity.ok(body);
    }
}
