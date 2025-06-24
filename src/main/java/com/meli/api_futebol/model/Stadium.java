package com.meli.api_futebol.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stadiumId;

    @Column(nullable = false)
    private String stadiumName;

    @Column(nullable = false)
    private String stadiumCity;

    @Column(nullable = false)
    private String stadiumOwner;
}
