package com.meli.api_futebol.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false, length = 2)
    private String teamState;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(name = "active",nullable = false)
    private boolean active = true;

}