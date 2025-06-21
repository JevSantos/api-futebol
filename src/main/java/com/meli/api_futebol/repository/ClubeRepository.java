package com.meli.api_futebol.repository;


import com.meli.api_futebol.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubeRepository extends JpaRepository<Team, Long> {

    Page<Team> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Team> findByEstado(String estado, Pageable pageable);
    Page<Team> findByAtivo(boolean ativo, Pageable pageable);
    Page<Team> findByNomeContainingIgnoreCaseAndEstado(String nome, String estado, Pageable pageable);
    Page<Team> findByNomeContainingIgnoreCaseAndAtivo(String nome, boolean ativo, Pageable pageable);
    Page<Team> findByEstadoAndAtivo(String estado, boolean ativo, Pageable pageable);
    Page<Team> findByNomeContainingIgnoreCaseAndEstadoAndAtivo(String nome, String estado, boolean ativo, Pageable pageable);
}