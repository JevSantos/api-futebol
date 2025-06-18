package com.meli.api_futebolv1.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Data
@Entity
@Where(clause = "ativo = true")
public class Clube {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @Column(nullable = false)
    private boolean ativo = true;
}