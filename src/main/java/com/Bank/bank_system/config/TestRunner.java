package com.Bank.bank_system.config;

import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Repository.ContaRepository;
import com.Bank.bank_system.Service.ContaService;
import com.Bank.bank_system.model.StatusConta;
import com.Bank.bank_system.model.TipoConta;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class TestRunner {

    @Bean
    CommandLineRunner run(ContaService contaService, ContaRepository contaRepository) {
        return args -> {

            // Criar contas de teste
            Conta conta1 = new Conta();
            conta1.setNumero("123");
            conta1.setSaldo(BigDecimal.ZERO);
            conta1.setTipo(TipoConta.CORRENTE);
            conta1.setStatus(StatusConta.ATIVA);

            Conta conta2 = new Conta();
            conta2.setNumero("456");
            conta2.setSaldo(BigDecimal.ZERO);
            conta2.setTipo(TipoConta.CORRENTE);
            conta2.setStatus(StatusConta.ATIVA);

            contaRepository.save(conta1);
            contaRepository.save(conta2);

            System.out.println("Contas criadas!");

            // Depositar
            contaService.depositar(conta1.getId(), new BigDecimal("500"));
            System.out.println("Depósito realizado!");

            // Sacar
            contaService.sacar(conta1.getId(), new BigDecimal("100"));
            System.out.println("Saque realizado!");

            // Transferir
            contaService.transferir(conta1.getId(), conta2.getId(), new BigDecimal("200"));
            System.out.println("Transferência realizada!");

        };
    }
}