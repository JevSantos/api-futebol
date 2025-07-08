package com.meli.api_futebol.dto;

import com.meli.api_futebol.model.Team;

public record TeamRetrospectDTO(
        Team team,
        Integer totalPlays,
        Integer victories,
        Integer ties,
        Integer loses,
        Integer goalsPro,
        Integer goalsCon
) {}