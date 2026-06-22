package com.Bank.bank_system.security;

import com.Bank.bank_system.Entity.Usuario;
import com.Bank.bank_system.model.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CurrentUser {

    public Usuario get() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        if (principal instanceof Usuario u) {
            return u;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida");
    }

    public boolean isAdmin() {
        Usuario u = getOrNull();
        return u != null && u.getRole() == Role.ADMIN;
    }

    public Long clienteId() {
        Usuario u = get();
        return u.getCliente() != null ? u.getCliente().getId() : null;
    }

    private Usuario getOrNull() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        return principal instanceof Usuario u ? u : null;
    }
}
