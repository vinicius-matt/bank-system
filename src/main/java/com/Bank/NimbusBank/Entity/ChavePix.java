package com.Bank.NimbusBank.Entity;

import com.Bank.NimbusBank.model.TipoChavePix;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chaves_pix")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChavePix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoChavePix tipo;

    @Column(nullable = false, unique = true)
    private String valor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @CreationTimestamp
    private LocalDateTime criadoEm;
}
