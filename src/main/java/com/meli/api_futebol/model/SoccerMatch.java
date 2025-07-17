package com.meli.api_futebol.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class SoccerMatch {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long soccerMatchId;

    @ManyToOne
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeamId;

    @ManyToOne
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeamId;

    @Column(name = "goals_home_team", nullable = false)
    private Integer goalsHomeTeam;

    @Column(name = "goals_away_team",nullable = false)
    private Integer goalsAwayTeam;

    @ManyToOne
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadiumId;

    @Column(name = "match_date_time",nullable = false)
    private LocalDateTime matchDateTime;
}