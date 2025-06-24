package com.meli.api_futebol.repository;

import com.meli.api_futebol.dto.RankingDTO;
import com.meli.api_futebol.dto.RetrospectVersusDTO;
import com.meli.api_futebol.model.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidaRepository extends JpaRepository<Match, Long> {
    Page<Match> findByClubeMandanteIdOrClubeVisitanteId(Long clubeId, Long clubeId2, Pageable pageable);
    Page<Match> findByEstadioId(Long estadioId, Pageable pageable);
    @Query("SELECT p FROM Match p WHERE " +
            "(p.homeTeam.teamId = :clubeId OR p.awayTeam.teamId = :clubeId) AND " +
            "ABS(p.goalsHomeTeam - p.goalsAwayTeam) >= 3")
    Page<Match> findGoleadasByClube(@Param("clubeId") Long clubeId, Pageable pageable);
    @Query("SELECT p FROM Match p WHERE " +
            "((p.homeTeam.teamId = :clube1Id AND p.awayTeam.teamId = :clube2Id) OR " +
            "(p.homeTeam.teamId = :clube2Id AND p.awayTeam.teamId = :clube1Id))")
    Page<Match> findConfrontos(@Param("clube1Id") Long clube1Id,
                               @Param("clube2Id") Long clube2Id,
                               Pageable pageable);
    @Query("SELECT p FROM Match p WHERE " +
            "((p.homeTeam.teamId = :clube1Id AND p.awayTeam.teamId = :clube2Id) OR " +
            "(p.homeTeam.teamId = :clube2Id AND p.awayTeam.teamId = :clube1Id)) AND " +
            "ABS(p.goalsHomeTeam - p.goalsAwayTeam) >= 3")
    Page<Match> findGoleadasConfrontos(@Param("clube1Id") Long clube1Id,
                                       @Param("clube2Id") Long clube2Id,
                                       Pageable pageable);
    @Query("SELECT " +
            "COUNT(p) as totalJogos, " +
            "SUM(CASE WHEN (p.homeTeam.teamId = :clubeId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = :clubeId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitorias, " +
            "SUM(CASE WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) as empates, " +
            "SUM(CASE WHEN (p.homeTeam.teamId = :clubeId AND p.goalsHomeTeam < p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = :clubeId AND p.goalsAwayTeam < p.goalsHomeTeam) THEN 1 ELSE 0 END) as derrotas, " +
            "SUM(CASE WHEN p.homeTeam.teamId = :clubeId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as golsFeitos, " +
            "SUM(CASE WHEN p.homeTeam.teamId = :clubeId THEN p.goalsAwayTeam ELSE p.goalsHomeTeam END) as golsSofridos " +
            "FROM Match p WHERE p.homeTeam.teamId = :clubeId OR p.awayTeam.teamId = :clubeId")
    List<Object[]> calcularRetrospecto(@Param("clubeId") Long clubeId);

    @Query("SELECT " +
            "a as adversario, " +
            "COUNT(p) as totalJogos, " +
            "SUM(CASE WHEN (p.homeTeam.teamId = :clubeId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = :clubeId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitorias, " +
            "SUM(CASE WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) as empates, " +
            "SUM(CASE WHEN (p.homeTeam.teamId = :clubeId AND p.goalsHomeTeam < p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = :clubeId AND p.goalsAwayTeam < p.goalsHomeTeam) THEN 1 ELSE 0 END) as derrotas, " +
            "SUM(CASE WHEN p.homeTeam.teamId = :clubeId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as golsFeitos, " +
            "SUM(CASE WHEN p.homeTeam.teamId = :clubeId THEN p.goalsAwayTeam ELSE p.goalsHomeTeam END) as golsSofridos " +
            "FROM Match p, Team a " +
            "WHERE (p.homeTeam.teamId = :clubeId AND p.awayTeam.teamId = a.teamId) OR " +
            "(p.awayTeam.teamId = :clubeId AND p.homeTeam.teamId = a.teamId) " +
            "GROUP BY a")
    List<RetrospectVersusDTO> calculateRetrospectVersusRivals(@Param("clubeId") Long clubeId);

    @Query("SELECT " +
            "COUNT(p) as totalJogos, " +
            "SUM(CASE WHEN (p.homeTeam.teamId = :clube1Id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = :clube1Id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitoriasClube1, " +
            "SUM(CASE WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) as empates, " +
            "SUM(CASE WHEN (p.homeTeam.teamId = :clube2Id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = :clube2Id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitoriasClube2, " +
            "SUM(CASE WHEN p.homeTeam.teamId = :clube1Id THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as golsClube1, " +
            "SUM(CASE WHEN p.homeTeam.teamId = :clube2Id THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as golsClube2, " +
            "SUM(CASE WHEN p.homeTeam.teamId = :clube1Id THEN (p.goalsHomeTeam - p.goalsAwayTeam) ELSE (p.goalsAwayTeam - p.goalsHomeTeam) END) as saldoClube1 " +
            "FROM Match p WHERE (p.homeTeam.teamId = :clube1Id AND p.awayTeam.teamId = :clube2Id) OR " +
            "(p.homeTeam.teamId = :clube2Id AND p.awayTeam.teamId = :clube1Id)")
    List<Object[]> calcularRetrospectoConfronto(@Param("clube1Id") Long clube1Id, @Param("clube2Id") Long clube2Id);

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, COUNT(p)) " +
            "FROM Match p JOIN Team c ON p.homeTeam.teamId = c.teamId OR p.awayTeam.teamId = c.teamId " +
            "GROUP BY c " +
            "HAVING COUNT(p) > 0 " +
            "ORDER BY COUNT(p) DESC")
    List<RankingDTO> rankearPorJogos();

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, " +
            "SUM(CASE WHEN (p.homeTeam.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END)) " +
            "FROM Match p JOIN Team c ON p.homeTeam.teamId = c.teamId OR p.awayTeam.teamId = c.teamId " +
            "GROUP BY c " +
            "HAVING SUM(CASE WHEN (p.homeTeam.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) > 0 " +
            "ORDER BY SUM(CASE WHEN (p.homeTeam.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) DESC")
    List<RankingDTO> rankearPorVitorias();

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, " +
            "SUM(CASE WHEN p.homeTeam.teamId = c.teamId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END)) " +
            "FROM Match p JOIN Team c ON p.homeTeam.teamId = c.teamId OR p.awayTeam.teamId = c.teamId " +
            "GROUP BY c " +
            "HAVING SUM(CASE WHEN p.homeTeam.teamId = c.teamId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) > 0 " +
            "ORDER BY SUM(CASE WHEN p.homeTeam.teamId = c.teamId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) DESC")
    List<RankingDTO> rankearPorGols();

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, " +
            "SUM(CASE WHEN (p.homeTeam.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 3 WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END)) " +
            "FROM Match p JOIN Team c ON p.homeTeam.teamId = c.teamId OR p.awayTeam.teamId = c.teamId " +
            "GROUP BY c " +
            "HAVING SUM(CASE WHEN (p.homeTeam.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 3 WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) > 0 " +
            "ORDER BY SUM(CASE WHEN (p.homeTeam.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeam.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 3 WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) DESC")
    List<RankingDTO> rankearPorPontos();

    @Query("SELECT p FROM Match p WHERE " +
            "(p.homeTeam.teamId = :clubeId OR p.awayTeam.teamId = :clubeId) AND " +
            "p.estadio.id = :estadioId")
    Page<Match> findByClubeMandanteIdOrClubeVisitanteIdAndEstadioId(
            @Param("clubeId") Long clubeId,
            @Param("estadioId") Long estadioId,
            Long id, Pageable pageable);

}