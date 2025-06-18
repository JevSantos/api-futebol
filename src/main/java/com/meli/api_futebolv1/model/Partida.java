package com.meli.api_futebolv1.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clube_mandante_id", nullable = false)
    private Clube clubeMandante;

    @ManyToOne
    @JoinColumn(name = "clube_visitante_id", nullable = false)
    private Clube clubeVisitante;

    @Column(nullable = false)
    private Integer golsMandante;

    @Column(nullable = false)
    private Integer golsVisitante;

    @ManyToOne
    @JoinColumn(name = "estadio_id", nullable = false)
    private Estadio estadio;

    @Column(nullable = false)
    private LocalDateTime dataHora;
}