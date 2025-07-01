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
public interface MatchRepository extends JpaRepository<Match, Long> {
    Page<Match> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId, Pageable pageable);

    Page<Match> findByStadiumId(Long stadiumId, Pageable pageable);

    @Query("SELECT p FROM Match p WHERE " +
            "(p.homeTeamId.teamId = :clubeId OR p.awayTeamId.teamId = :teamId) AND " +
            "ABS(p.goalsHomeTeam - p.goalsAwayTeam) >= 3")
    Page<Match> findLandslideByTeam(@Param("teamId") Long teamId, Pageable pageable);

    @Query("SELECT p FROM Match p WHERE " +
            "((p.homeTeamId.teamId = :team1Id AND p.awayTeamId.teamId = :team2Id) OR " +
            "(p.homeTeamId.teamId = :team2Id AND p.awayTeamId.teamId = :team1Id))")
    Page<Match> findClashes(@Param("team1Id") Long team1Id,
                            @Param("team2Id") Long team2Id,
                            Pageable pageable);
    @Query("SELECT p FROM Match p WHERE " +
            "((p.homeTeamId.teamId = :team1Id AND p.awayTeamId.teamId = :team2Id) OR " +
            "(p.homeTeamId.teamId = :team2Id AND p.awayTeamId.teamId = :team1Id)) AND " +
            "ABS(p.goalsHomeTeam - p.goalsAwayTeam) >= 3")
    Page<Match> findLandslideClashes(@Param("team1Id") Long team1Id,
                                     @Param("team2Id") Long team2Id,
                                     Pageable pageable);

    @Query("SELECT p FROM Match p WHERE " +
            "(p.homeTeamId.teamId = :teamId OR p.awayTeamId.teamId = :teamId) AND " +
            "p.stadium.stadiumId = :stadiumId")
    Page<Match> findByHomeTeamIdOrAwayTeamIdAndStadiumId(
            @Param("teamId") Long teamId, @Param("stadiumId") Long stadiumId, Pageable pageable);

    @Query("SELECT " +
            "COUNT(p) as totalJogos, " +
            "SUM(CASE WHEN (p.homeTeamId.teamId = :teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = :teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitorias, " +
            "SUM(CASE WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) as empates, " +
            "SUM(CASE WHEN (p.homeTeamId.teamId = :teamId AND p.goalsHomeTeam < p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = :teamId AND p.goalsAwayTeam < p.goalsHomeTeam) THEN 1 ELSE 0 END) as derrotas, " +
            "SUM(CASE WHEN p.homeTeamId.teamId = :teamId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as golsFeitos, " +
            "SUM(CASE WHEN p.homeTeamId.teamId = :teamId THEN p.goalsAwayTeam ELSE p.goalsHomeTeam END) as golsSofridos " +
            "FROM Match p WHERE p.homeTeamId.teamId = :teamId OR p.awayTeamId.teamId = :teamId")
    List<Object[]> calculateRetrospect(@Param("teamId") Long teamId);

    @Query("SELECT " +
            "a as adversario, " +
            "COUNT(p) as totalJogos, " +
            "SUM(CASE WHEN (p.homeTeamId.teamId = :teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = :teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitorias, " +
            "SUM(CASE WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) as empates, " +
            "SUM(CASE WHEN (p.homeTeamId.teamId = :teamId AND p.goalsHomeTeam < p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = :teamId AND p.goalsAwayTeam < p.goalsHomeTeam) THEN 1 ELSE 0 END) as derrotas, " +
            "SUM(CASE WHEN p.homeTeamId.teamId = :teamId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as golsFeitos, " +
            "SUM(CASE WHEN p.homeTeamId.teamId = :teamId THEN p.goalsAwayTeam ELSE p.goalsHomeTeam END) as golsSofridos " +
            "FROM Match p, Team a " +
            "WHERE (p.homeTeamId.teamId = :teamId AND p.awayTeamId.teamId = a.teamId) OR " +
            "(p.awayTeamId.teamId = :teamId AND p.homeTeamId.teamId = a.teamId) " +
            "GROUP BY a")
    List<RetrospectVersusDTO> calculateRetrospectVersusRivals(@Param("teamId") Long teamId);

    @Query("SELECT " +
            "COUNT(p) as totalJogos, " +
            "SUM(CASE WHEN (p.homeTeamId.teamId = :team1Id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = :team1Id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitoriasTeam1, " +
            "SUM(CASE WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) as empates, " +
            "SUM(CASE WHEN (p.homeTeamId.teamId = :team2Id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = :team2Id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitoriasTeam2, " +
            "SUM(CASE WHEN p.homeTeamId.teamId = :team1Id THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as goalsTeam1, " +
            "SUM(CASE WHEN p.homeTeamId.teamId = :team2Id THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as goalsTeam2, " +
            "SUM(CASE WHEN p.homeTeamId.teamId = :team1Id THEN (p.goalsHomeTeam - p.goalsAwayTeam) ELSE (p.goalsAwayTeam - p.goalsHomeTeam) END) as goalsBalance " +
            "FROM Match p WHERE (p.homeTeamId.teamId = :team1Id AND p.awayTeamId.teamId = :team2Id) OR " +
            "(p.homeTeamId.teamId = :team2Id AND p.awayTeamId.teamId = :team1Id)")
    List<Object[]> calculateClashesRetrospect(@Param("team1Id") Long team1Id, @Param("team2Id") Long team2Id);

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, COUNT(p)) " +
            "FROM Match p JOIN Team c ON p.homeTeamId.teamId = c.teamId OR p.awayTeamId.teamId = c.teamId " +
            "GROUP BY c " +
            "HAVING COUNT(p) > 0 " +
            "ORDER BY COUNT(p) DESC")
    List<RankingDTO> rankingByPlays();

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, " +
            "SUM(CASE WHEN (p.homeTeamId.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END)) " +
            "FROM Match p JOIN Team c ON p.homeTeamId.teamId = c.teamId OR p.awayTeamId.teamId = c.teamId " +
            "GROUP BY c " +
            "HAVING SUM(CASE WHEN (p.homeTeamId.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) > 0 " +
            "ORDER BY SUM(CASE WHEN (p.homeTeamId.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) DESC")
    List<RankingDTO> rankingByVictories();

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, " +
            "SUM(CASE WHEN p.homeTeamId.teamId = c.teamId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END)) " +
            "FROM Match p JOIN Team c ON p.homeTeamId.teamId = c.teamId OR p.awayTeamId.teamId = c.teamId " +
            "GROUP BY c " +
            "HAVING SUM(CASE WHEN p.homeTeamId.teamId = c.teamId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) > 0 " +
            "ORDER BY SUM(CASE WHEN p.homeTeamId.teamId = c.teamId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) DESC")
    List<RankingDTO> rankingByGoals();

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, " +
            "SUM(CASE WHEN (p.homeTeamId.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 3 WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END)) " +
            "FROM Match p JOIN Team c ON p.homeTeamId.teamId = c.teamId OR p.awayTeamId.teamId = c.teamId " +
            "GROUP BY c " +
            "HAVING SUM(CASE WHEN (p.homeTeamId.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 3 WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) > 0 " +
            "ORDER BY SUM(CASE WHEN (p.homeTeamId.teamId = c.teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.teamId = c.teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 3 WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) DESC")
    List<RankingDTO> rankingByPoints();
}