package com.meli.api_futebolv1.repository;


import com.meli.api_futebolv1.model.Partida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
    Page<Partida> findByClubeMandanteIdOrClubeVisitanteId(Long clubeMandanteId, Long clubeVisitanteId, Pageable pageable);
    Page<Partida> findByEstadioContainingIgnoreCase(String estadio, Pageable pageable);
}
