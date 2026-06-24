package com.Bank.NimbusBank.Entity;

import com.Bank.NimbusBank.model.StatusConta;
import com.Bank.NimbusBank.model.TipoConta;
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

    @Column(precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal limite;

    @Enumerated(EnumType.STRING)
    private TipoConta tipo;

    @Enumerated(EnumType.STRING)
    private StatusConta status;

    @Version
    private Long version;
}
