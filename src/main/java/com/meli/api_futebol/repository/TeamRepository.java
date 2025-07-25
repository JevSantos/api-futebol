package com.meli.api_futebol.repository;


import com.meli.api_futebol.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Page<Team> findByTeamNameContainingIgnoreCase(String teamName, Pageable pageable);
    Page<Team> findByTeamState(String teamState, Pageable pageable);
    Page<Team> findByActive(boolean active, Pageable pageable);

    Page<Team> findByTeamNameContainingIgnoreCaseAndTeamState(String teamName, String teamState, Pageable pageable);
    Page<Team> findByTeamNameContainingIgnoreCaseAndActive(String teamName, boolean active, Pageable pageable);
    Page<Team> findByTeamStateAndActive(String teamState, boolean active, Pageable pageable);
    Page<Team> findByTeamNameContainingIgnoreCaseAndTeamStateAndActive(String teamName, String teamState, boolean active, Pageable pageable);
}