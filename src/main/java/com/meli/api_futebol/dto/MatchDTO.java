package com.meli.api_futebol.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MatchDTO(
        @NotNull(message = "ID do team mandante é obrigatório")
        Long homeTeamId,
        @NotNull(message = "ID do team visitante é obrigatório")
        Long awayTeamId,
        @NotNull(message = "Gols do mandante são obrigatórios")
        Integer goalsHomeTeam,
        @NotNull(message = "Gols do visitante são obrigatórios")
        Integer goalsAwayTeam,
        @NotNull(message = "ID do estádio é obrigatório")
        Long stadiumId,
        LocalDateTime matchDateTime
) {}
