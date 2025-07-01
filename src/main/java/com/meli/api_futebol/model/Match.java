package com.meli.api_futebol.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long match_id;

    @ManyToOne
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeamId;

    @ManyToOne
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeamId;

    @Column(nullable = false)
    private Integer goalsHomeTeam;

    @Column(nullable = false)
    private Integer goalsAwayTeam;

    @ManyToOne
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;

    @Column(nullable = false)
    private LocalDateTime matchDateTime;
}