package com.meli.api_futebolv1.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Estadio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cidade;
}
