package com.meli.api_futebol.dto;

import com.meli.api_futebol.model.Team;

public record RankingDTO(
        Team team,
        Long valor
) {}
