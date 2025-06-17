package com.meli.api_futebolv1.dto;


import java.time.LocalDate;

public record ClubeDTO(
        String nome,
        String estado,
        LocalDate dataCriacao,
        Boolean ativo
) {}
