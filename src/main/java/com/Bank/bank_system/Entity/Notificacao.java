package com.Bank.bank_system.Entity;

import com.Bank.bank_system.model.TipoNotificacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String titulo;

    @Column(length = 500)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    private boolean lida;

    @CreationTimestamp
    private LocalDateTime criadoEm;
}
