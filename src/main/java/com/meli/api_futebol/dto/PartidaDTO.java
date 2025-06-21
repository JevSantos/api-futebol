package com.meli.api_futebol.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PartidaDTO(
        @NotNull(message = "ID do clube mandante é obrigatório")
        Long clubeMandanteId,

        @NotNull(message = "ID do clube visitante é obrigatório")
        Long clubeVisitanteId,

        @NotNull(message = "Gols do mandante são obrigatórios")
        Integer golsMandante,

        @NotNull(message = "Gols do visitante são obrigatórios")
        Integer golsVisitante,

        @NotNull(message = "ID do estádio é obrigatório")
        Long estadioId,

        LocalDateTime dataHora
) {}
