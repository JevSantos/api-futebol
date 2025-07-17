package com.meli.api_futebol.repository;

import com.meli.api_futebol.dto.RankingDTO;
import com.meli.api_futebol.dto.RetrospectVersusDTO;
import com.meli.api_futebol.model.SoccerMatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoccerMatchRepository extends JpaRepository<SoccerMatch, Long> {
    Page<SoccerMatch> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId, Pageable pageable);

    Page<SoccerMatch> findByStadiumId(Long stadiumId, Pageable pageable);

    @Query("SELECT p FROM SoccerMatch p WHERE " +
            "(p.homeTeamId.id = :teamId OR p.awayTeamId.id = :teamId) AND " +
            "ABS(p.goalsHomeTeam - p.goalsAwayTeam) >= 3")
    Page<SoccerMatch> findLandslideByTeam(@Param("teamId") Long teamId, Pageable pageable);

    @Query("SELECT p FROM SoccerMatch p WHERE " +
            "((p.homeTeamId.id = :team1Id AND p.awayTeamId.id = :team2Id) OR " +
            "(p.homeTeamId.id = :team2Id AND p.awayTeamId.id = :team1Id))")
    Page<SoccerMatch> findClashes(@Param("team1Id") Long team1Id,
                                  @Param("team2Id") Long team2Id,
                                  Pageable pageable);
    @Query("SELECT p FROM SoccerMatch p WHERE " +
            "((p.homeTeamId.id = :team1Id AND p.awayTeamId.id = :team2Id) OR " +
            "(p.homeTeamId.id = :team2Id AND p.awayTeamId.id = :team1Id)) AND " +
            "ABS(p.goalsHomeTeam - p.goalsAwayTeam) >= 3")
    Page<SoccerMatch> findLandslideClashes(@Param("team1Id") Long team1Id,
                                           @Param("team2Id") Long team2Id,
                                           Pageable pageable);

    @Query("SELECT p FROM SoccerMatch p WHERE " +
            "(p.homeTeamId.id = :teamId OR p.awayTeamId.id = :teamId) AND " +
            "p.stadiumId = :stadiumId")
    Page<SoccerMatch> findByHomeTeamIdOrAwayTeamIdAndStadiumId(
            @Param("teamId") Long teamId, @Param("stadiumId") Long stadiumId, Pageable pageable);

    @Query("SELECT " +
            "COUNT(p) as totalJogos, " +
            "SUM(CASE WHEN (p.homeTeamId.id = :teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = :teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitorias, " +
            "SUM(CASE WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) as empates, " +
            "SUM(CASE WHEN (p.homeTeamId.id = :teamId AND p.goalsHomeTeam < p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = :teamId AND p.goalsAwayTeam < p.goalsHomeTeam) THEN 1 ELSE 0 END) as derrotas, " +
            "SUM(CASE WHEN p.homeTeamId.id = :teamId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as golsFeitos, " +
            "SUM(CASE WHEN p.homeTeamId.id = :teamId THEN p.goalsAwayTeam ELSE p.goalsHomeTeam END) as golsSofridos " +
            "FROM SoccerMatch p WHERE p.homeTeamId.id = :teamId OR p.awayTeamId.id = :teamId")
    List<Object[]> calculateRetrospect(@Param("teamId") Long teamId);

    @Query("SELECT " +
            "a as adversario, " +
            "COUNT(p) as totalJogos, " +
            "SUM(CASE WHEN (p.homeTeamId.id = :teamId AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = :teamId AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitorias, " +
            "SUM(CASE WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) as empates, " +
            "SUM(CASE WHEN (p.homeTeamId.id = :teamId AND p.goalsHomeTeam < p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = :teamId AND p.goalsAwayTeam < p.goalsHomeTeam) THEN 1 ELSE 0 END) as derrotas, " +
            "SUM(CASE WHEN p.homeTeamId.id = :teamId THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as golsFeitos, " +
            "SUM(CASE WHEN p.homeTeamId.id = :teamId THEN p.goalsAwayTeam ELSE p.goalsHomeTeam END) as golsSofridos " +
            "FROM SoccerMatch p, Team a " +
            "WHERE (p.homeTeamId.id = :teamId AND p.awayTeamId.id = a.id) OR " +
            "(p.awayTeamId.id = :teamId AND p.homeTeamId.id = a.id) " +
            "GROUP BY a")
    List<RetrospectVersusDTO> calculateRetrospectVersusRivals(@Param("teamId") Long teamId);

    @Query("SELECT " +
            "COUNT(p) as totalJogos, " +
            "SUM(CASE WHEN (p.homeTeamId.id = :team1Id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = :team1Id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitoriasTeam1, " +
            "SUM(CASE WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) as empates, " +
            "SUM(CASE WHEN (p.homeTeamId.id = :team2Id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = :team2Id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) as vitoriasTeam2, " +
            "SUM(CASE WHEN p.homeTeamId.id = :team1Id THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as goalsTeam1, " +
            "SUM(CASE WHEN p.homeTeamId.id = :team2Id THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) as goalsTeam2, " +
            "SUM(CASE WHEN p.homeTeamId.id = :team1Id THEN (p.goalsHomeTeam - p.goalsAwayTeam) ELSE (p.goalsAwayTeam - p.goalsHomeTeam) END) as goalsBalance " +
            "FROM SoccerMatch p WHERE (p.homeTeamId.id = :team1Id AND p.awayTeamId.id = :team2Id) OR " +
            "(p.homeTeamId.id = :team2Id AND p.awayTeamId.id = :team1Id)")
    List<Object[]> calculateClashesRetrospect(@Param("team1Id") Long team1Id, @Param("team2Id") Long team2Id);

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, COUNT(p)) " +
            "FROM SoccerMatch p JOIN Team c ON p.homeTeamId.id = c.id OR p.awayTeamId.id = c.id " +
            "GROUP BY c " +
            "HAVING COUNT(p) > 0 " +
            "ORDER BY COUNT(p) DESC")
    List<RankingDTO> rankingByPlays();

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, " +
            "SUM(CASE WHEN (p.homeTeamId.id = c.id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = c.id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END)) " +
            "FROM SoccerMatch p JOIN Team c ON p.homeTeamId.id = c.id OR p.awayTeamId.id = c.id " +
            "GROUP BY c " +
            "HAVING SUM(CASE WHEN (p.homeTeamId.id = c.id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = c.id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) > 0 " +
            "ORDER BY SUM(CASE WHEN (p.homeTeamId.id = c.id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = c.id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 1 ELSE 0 END) DESC")
    List<RankingDTO> rankingByVictories();

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, " +
            "SUM(CASE WHEN p.homeTeamId.id = c.id THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END)) " +
            "FROM SoccerMatch p JOIN Team c ON p.homeTeamId.id = c.id OR p.awayTeamId.id = c.id " +
            "GROUP BY c " +
            "HAVING SUM(CASE WHEN p.homeTeamId.id = c.id THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) > 0 " +
            "ORDER BY SUM(CASE WHEN p.homeTeamId.id = c.id THEN p.goalsHomeTeam ELSE p.goalsAwayTeam END) DESC")
    List<RankingDTO> rankingByGoals();

    @Query("SELECT new com.meli.api_futebol.dto.RankingDTO(c, " +
            "SUM(CASE WHEN (p.homeTeamId.id = c.id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = c.id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 3 WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END)) " +
            "FROM SoccerMatch p JOIN Team c ON p.homeTeamId.id = c.id OR p.awayTeamId.id = c.id " +
            "GROUP BY c " +
            "HAVING SUM(CASE WHEN (p.homeTeamId.id = c.id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = c.id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 3 WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) > 0 " +
            "ORDER BY SUM(CASE WHEN (p.homeTeamId.id = c.id AND p.goalsHomeTeam > p.goalsAwayTeam) OR " +
            "(p.awayTeamId.id = c.id AND p.goalsAwayTeam > p.goalsHomeTeam) THEN 3 WHEN p.goalsHomeTeam = p.goalsAwayTeam THEN 1 ELSE 0 END) DESC")
    List<RankingDTO> rankingByPoints();
}