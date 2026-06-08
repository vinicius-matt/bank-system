package com.Bank.bank_system.config;

import com.Bank.bank_system.Entity.Cliente;
import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Repository.ClienteRepository;
import com.Bank.bank_system.Repository.ContaRepository;
import com.Bank.bank_system.Service.ContaService;
import com.Bank.bank_system.dto.TransacaoResponseDTO;
import com.Bank.bank_system.model.StatusConta;
import com.Bank.bank_system.model.TipoConta;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

//@Configuration
public class TestRunner {

    @Bean
    CommandLineRunner run(
            ContaService contaService,
            ContaRepository contaRepository,
            ClienteRepository clienteRepository) {

        return args -> {

            // Criar Cliente
            Cliente cliente = new Cliente();
            cliente.setNome("Matheus");
            cliente.setCpf("12345678900");
            cliente.setEmail("matheus@email.com");

            System.out.println(cliente);
            clienteRepository.save(cliente);

            System.out.println("Cliente criado!");

            // Criar Conta 1
            Conta conta1 = new Conta();
            conta1.setNumero("123");
            conta1.setSaldo(BigDecimal.ZERO);
            conta1.setTipo(TipoConta.CORRENTE);
            conta1.setStatus(StatusConta.ATIVA);
            conta1.setCliente(cliente);


            // Criar Conta 2
            Conta conta2 = new Conta();
            conta2.setNumero("456");
            conta2.setSaldo(BigDecimal.ZERO);
            conta2.setTipo(TipoConta.CORRENTE);
            conta2.setStatus(StatusConta.ATIVA);
            conta2.setCliente(cliente);

            contaRepository.save(conta1);
            contaRepository.save(conta2);

            System.out.println("Contas criadas!");

            //Deposita
            contaService.depositar(conta1.getId(), new BigDecimal("500"));
            System.out.println("Depósito realizado!");

            // Saco
            contaService.sacar(conta1.getId(), new BigDecimal("100"));
            System.out.println("Saque realizado!");

            //Transfere
            contaService.transferir(conta1.getId(), conta2.getId(), new BigDecimal("200"));
            System.out.println("Transferência realizada!");

            // ve saldo
            Conta c1 = contaRepository.findById(conta1.getId()).orElseThrow();
            Conta c2 = contaRepository.findById(conta2.getId()).orElseThrow();

            System.out.println("Saldo Conta 1: " + c1.getSaldo());
            System.out.println("Saldo Conta 2: " + c2.getSaldo());

            System.out.println("=== EXTRATO CONTA 1 ===");

            List<TransacaoResponseDTO> extrato = contaService.extrato(conta1.getId());

            for (TransacaoResponseDTO t : extrato) {
                System.out.println(
                        t.getTipo() + " | " +
                                t.getValor() + " | " +
                                t.getDescricao()
                );
            }
        };
    }
}
