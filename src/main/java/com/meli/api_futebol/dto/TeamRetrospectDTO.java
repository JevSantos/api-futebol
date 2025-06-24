package com.meli.api_futebol.dto;

import com.meli.api_futebol.model.Team;

public record TeamRetrospectDTO(
        Team team,
        Integer totalPlays,
        Integer vitorias,
        Integer empates,
        Integer derrotas,
        Integer golsFeitos,
        Integer golsSofridos
) {}