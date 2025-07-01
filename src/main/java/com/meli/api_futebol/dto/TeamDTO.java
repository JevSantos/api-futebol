package com.meli.api_futebol.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TeamDTO(
        @NotBlank(message = "Nome do team é obrigatório")
        String teamName,

        @NotBlank(message = "Sigla do teamState é obrigatória")
        @Size(min = 2, max = 2, message = "A sigla do teamState deve ter 2 caracteres")
        String teamState,

        @NotNull(message = "Data de criação é obrigatória")
        LocalDate creationDate
) {}
