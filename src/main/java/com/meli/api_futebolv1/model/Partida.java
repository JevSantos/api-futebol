package com.meli.api_futebolv1.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Clube clubeMandante;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Clube clubeVisitante;

    @Column(nullable = false)
    private String placar;

    @Column(nullable = false)
    private String estadio;

    @Column(nullable = false)
    private LocalDateTime dataHora;
}
