package com.meli.api_futebol.dto;

import com.meli.api_futebol.model.Team;

public record RetrospectVersusDTO(
        Team homeTeam,
        Team awayTeam,
        Integer totalPlays,
        Integer homeTeamVictories,
        Integer drawsQtd,
        Integer awayTeamVictories,
        Integer goalsHomeTeam,
        Integer goalsAwayTeam,
        Integer goalsBalance
) {}