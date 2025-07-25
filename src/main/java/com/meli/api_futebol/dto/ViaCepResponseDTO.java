package com.meli.api_futebol.dto;

import lombok.Data;

@Data
public class ViaCepResponseDTO {
    private String cep;
    private String logradouro; // address
    private String bairro;    // neighborhood
    private String localidade; // city (can map to locationOfStadium if preferred)
    private String uf;         // state
}
