package com.meli.api_futebol.dto;

import com.meli.api_futebol.model.Team;

public record TeamRetrospectDTO(
        Team team,
        Long totalJogos,
        Long vitorias,
        Long empates,
        Long derrotas,
        Long golsFeitos,
        Long golsSofridos
) {}