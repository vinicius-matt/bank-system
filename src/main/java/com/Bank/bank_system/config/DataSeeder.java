package com.Bank.bank_system.config;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Entity.Usuario;
import com.Bank.bank_system.Repository.UsuarioRepository;
import com.Bank.bank_system.model.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test")
public class DataSeeder {

    /**
     * Cria contas padrão na primeira execução, para autenticar imediatamente:
     *
     *  - ADMIN (operador, sem perfil de titular)
     *      email: admin@bank.com   senha: admin123
     *
     *  - USER comum (já com perfil de cliente vinculado, modelo 1:1)
     *      email: user@bank.com    senha: user123
     */
    @Bean
    CommandLineRunner seedUsuarios(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!usuarioRepository.existsByEmail("admin@bank.com")) {
                Usuario admin = Usuario.builder()
                        .nome("Administrador")
                        .email("admin@bank.com")
                        .senha(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build();
                usuarioRepository.save(admin);
                System.out.println(">> Usuário admin padrão criado: admin@bank.com / admin123");
            }

            if (!usuarioRepository.existsByEmail("user@bank.com")) {
                // Perfil de titular vinculado (cascade ALL persiste o Cliente junto)
                Cliente cliente = new Cliente();
                cliente.setNome("Cliente Demonstração");
                cliente.setCpf("00000000000");
                cliente.setEmail("user@bank.com");
                cliente.setCelular("11999990000");

                Usuario user = Usuario.builder()
                        .nome("Cliente Demonstração")
                        .email("user@bank.com")
                        .senha(passwordEncoder.encode("user123"))
                        .role(Role.USER)
                        .cliente(cliente)
                        .build();
                usuarioRepository.save(user);
                System.out.println(">> Usuário comum padrão criado: user@bank.com / user123 (com perfil)");
            }
        };
    }
}
