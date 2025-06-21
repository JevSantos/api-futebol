package com.meli.api_futebol.dto;

import jakarta.validation.constraints.NotBlank;

public record EstadioDTO(
        @NotBlank(message = "Nome do estádio é obrigatório")
        String nome,

        @NotBlank(message = "Cidade do estádio é obrigatória")
        String cidade
) {}