package com.Bank.bank_system.config;

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
     * Cria um usuário administrador padrão na primeira execução,
     * para que seja possível autenticar imediatamente.
     *   email: admin@bank.com
     *   senha: admin123
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
        };
    }
}
