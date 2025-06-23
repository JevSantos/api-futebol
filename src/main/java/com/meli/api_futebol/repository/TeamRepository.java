package com.meli.api_futebol.repository;


import com.meli.api_futebol.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Page<Team> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Team> findByState(String state, Pageable pageable);
    Page<Team> findByActive(boolean active, Pageable pageable);
    Page<Team> findByNameContainingIgnoreCaseAndState(String name, String state, Pageable pageable);
    Page<Team> findByNameContainingIgnoreCaseAndActive(String name, boolean active, Pageable pageable);
    Page<Team> findByStateAndActive(String state, boolean active, Pageable pageable);
    Page<Team> findByNameContainingIgnoreCaseAndStateAndActive(String name, String state, boolean active, Pageable pageable);
}