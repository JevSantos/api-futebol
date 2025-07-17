package com.meli.api_futebol.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_name",nullable = false)
    private String teamName;

    @Column(name = "team_state", nullable = false, length = 2)
    private String teamState;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "active",nullable = false)
    private boolean active = true;

}