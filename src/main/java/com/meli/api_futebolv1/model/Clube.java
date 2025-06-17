package com.meli.api_futebolv1.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Clube {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false)
    private LocalDate dataCriacao;

    @Column(nullable = false)
    private Boolean ativo = true;
}
