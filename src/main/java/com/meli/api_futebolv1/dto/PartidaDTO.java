package com.meli.api_futebolv1.dto;

import java.time.LocalDateTime;

public record PartidaDTO(
        Long clubeMandanteId,
        Long clubeVisitanteId,
        String placar,
        String estadio,
        LocalDateTime dataHora
) {}
