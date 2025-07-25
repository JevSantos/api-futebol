package com.meli.api_futebol.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StadiumDTO(
        @NotBlank(message = "Nome do estádio é obrigatório")
        String stadiumName,
        //@NotBlank(message = "Cidade do estádio é obrigatória")
        //String stadiumCity,
        @NotBlank(message = "O nome proprietário do estádio é obrigatório")
        String stadiumOwner,

        @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve ter 8")
        String cep
) {}