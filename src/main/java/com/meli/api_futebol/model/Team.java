package com.meli.api_futebol.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Data
@Entity
@Where(clause = "ativo = true")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false, length = 2)
    private String teamState;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    private boolean active = true;
}