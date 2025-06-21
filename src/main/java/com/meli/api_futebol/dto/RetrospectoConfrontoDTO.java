package com.meli.api_futebol.dto;

import com.meli.api_futebol.model.Team;

public record RetrospectoConfrontoDTO(
        Team clube1,
        Team clube2,
        Long totalJogos,
        Long vitoriasClube1,
        Long empates,
        Long vitoriasClube2,
        Long golsClube1,
        Long golsClube2,
        Long saldoClube1
) {}