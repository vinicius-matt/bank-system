package com.Bank.NimbusBank.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    @Email(message = "Email inválido")
    private String email;

    @Column(unique = true)
    @NotBlank
    private String celular;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente")
    private List<Conta> contas;


}
