package com.Bank.bank_system.Entity;

import com.Bank.bank_system.model.StatusConta;
import com.Bank.bank_system.model.TipoConta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private BigDecimal saldo =  BigDecimal.ZERO;

    private BigDecimal limite;

    @Enumerated(EnumType.STRING)
    private TipoConta tipo;

    @Enumerated(EnumType.STRING)
    private StatusConta status;

    /** Controle de concorrência otimista: evita corromper o saldo em operações simultâneas. */
    @Version
    private Long version;
}
