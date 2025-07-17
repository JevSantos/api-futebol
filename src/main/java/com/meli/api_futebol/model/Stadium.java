package com.meli.api_futebol.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stadiumId;

    @Column(name = "stadium_name",nullable = false)
    private String stadiumName;

    @Column(name = "stadium_city",nullable = false)
    private String stadiumCity;

    @Column(name = "stadium_owner",nullable = false)
    private String stadiumOwner;
}
