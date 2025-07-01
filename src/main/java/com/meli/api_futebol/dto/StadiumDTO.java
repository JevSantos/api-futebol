package com.meli.api_futebol.dto;

import jakarta.validation.constraints.NotBlank;

public record StadiumDTO(
        @NotBlank(message = "Nome do estádio é obrigatório")
        String stadiumName,
        @NotBlank(message = "Cidade do estádio é obrigatória")
        String stadiumCity,
        @NotBlank(message = "O nome proprietário do estádio é obrigatório")
        String stadiumOwner

) {}