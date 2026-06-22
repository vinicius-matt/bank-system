package com.Bank.NimbusBank.config;

import com.Bank.NimbusBank.Entity.Cliente;
import com.Bank.NimbusBank.Entity.Usuario;
import com.Bank.NimbusBank.Repository.UsuarioRepository;
import com.Bank.NimbusBank.model.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test")
public class DataSeeder {


    //Criar usuarios de teste para eu testar o sistema
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
                System.out.println(">> Usuario admin padrao criado: admin@bank.com / admin123");
            }

            if (!usuarioRepository.existsByEmail("user@bank.com")) {
                Cliente cliente = new Cliente();
                cliente.setNome("Cliente Demonstração");
                cliente.setCpf("00000000000");
                cliente.setEmail("user@bank.com");
                cliente.setCelular("11999990000");

                Usuario user = Usuario.builder()
                        .nome("Cliente Teste")
                        .email("user@bank.com")
                        .senha(passwordEncoder.encode("user123"))
                        .role(Role.USER)
                        .cliente(cliente)
                        .build();
                usuarioRepository.save(user);
                System.out.println(">> Usuario comum padrao criado: user@bank.com / user123 (com perfil)");
            }
        };
    }
}
