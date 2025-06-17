package com.meli.api_futebolv1.repository;

import com.meli.api_futebolv1.model.Clube;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubeRepository extends JpaRepository<Clube, Long> {
    Page<Clube> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Clube> findByEstado(String estado, Pageable pageable);
    Page<Clube> findByAtivo(Boolean ativo, Pageable pageable);
}
