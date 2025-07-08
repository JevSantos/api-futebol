package com.meli.api_futebol.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Team homeTeamId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Team awayTeamId;

    @Column(nullable = false)
    private Integer goalsHomeTeam;

    @Column(nullable = false)
    private Integer goalsAwayTeam;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Stadium stadiumId;

    @Column(nullable = false)
    private LocalDateTime matchDateTime;
}