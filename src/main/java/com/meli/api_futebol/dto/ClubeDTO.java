package com.meli.api_futebol.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ClubeDTO(
        @NotBlank(message = "Nome do clube é obrigatório")
        String nome,

        @NotBlank(message = "Sigla do estado é obrigatória")
        @Size(min = 2, max = 2, message = "A sigla do estado deve ter 2 caracteres")
        String estado,

        @NotNull(message = "Data de criação é obrigatória")
        LocalDate dataCriacao
) {}
