package com.meli.api_futebol.service;


import com.meli.api_futebol.dto.*;
import com.meli.api_futebol.model.*;
import com.meli.api_futebol.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;

    private Team findTeamById(Long teamId, String errorMessage) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage));
    }

    private Stadium findStadiumById(Long stadiumId, String errorMessage) {
        return stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage));
    }

    public Match createMatch(MatchDTO dto) {
        Match match = new Match();

        Team homeTeam = findTeamById(dto.homeTeamId(), "Clube mandante não encontrado");
        Team awayTeam = findTeamById(dto.awayTeamId(), "Clube visitante não encontrado");
        Stadium stadium = findStadiumById(dto.stadiumId(), "Estádio não encontrado");
        if (homeTeam.getTeamId().equals(awayTeam.getTeamId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O team mandante e o team visitante não podem ser o mesmo.");
        }

        match.setHomeTeamId(homeTeam);
        match.setAwayTeamId(awayTeam);
        match.setGoalsHomeTeam(dto.goalsHomeTeam());
        match.setGoalsAwayTeam(dto.goalsAwayTeam());
        match.setStadiumId(stadium);
        match.setMatchDateTime(dto.matchDateTime() != null ? dto.matchDateTime() : LocalDateTime.now());

        return matchRepository.save(match);
    }

    public Match updateMatch(Long id, MatchDTO dto) {
        Match match = findMatchById(id);

        if (dto.homeTeamId() != null) {
            Team newHomeTeam = findTeamById(dto.homeTeamId(), "Clube mandante não encontrado");
            if (newHomeTeam.getTeamId().equals(match.getAwayTeamId().getTeamId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O novo team mandante não pode ser igual ao team visitante atual.");
            }
            match.setHomeTeamId(newHomeTeam);
        }
        if (dto.awayTeamId() != null) {
            Team newAwayTeam = findTeamById(dto.awayTeamId(), "Clube visitante não encontrado");
            if (newAwayTeam.getTeamId().equals(match.getHomeTeamId().getTeamId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O novo team visitante não pode ser igual ao team mandante atual.");
            }
            match.setAwayTeamId(newAwayTeam);
        }
        if (dto.stadiumId() != null) {
            Stadium stadium = findStadiumById(dto.stadiumId(), "Estádio não encontrado");
            match.setStadiumId(stadium);
        }
        if (dto.goalsHomeTeam() != null) match.setGoalsHomeTeam(dto.goalsHomeTeam());
        if (dto.goalsAwayTeam() != null) match.setGoalsAwayTeam(dto.goalsAwayTeam());
        if (dto.matchDateTime() != null) match.setMatchDateTime(dto.matchDateTime());

        return matchRepository.save(match);
    }

    public void removeMatch(Long id) {
        Match match = findMatchById(id);
        matchRepository.delete(match);
    }

    public Match findMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada"));
    }

    public Page<Match> listMatch(Long teamId, Long stadiumId, Pageable pageable) {
        if (teamId != null && stadiumId != null) {
            return matchRepository.findByHomeTeamIdOrAwayTeamIdAndStadiumId(teamId, stadiumId, pageable);
        } else if (teamId != null) {
            return matchRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId, pageable);
        } else if (stadiumId != null) {
            return matchRepository.findByStadiumId(stadiumId, pageable);
        }
        return matchRepository.findAll(pageable);
    }

    public Page<Match> listLandslides(Long teamId, Pageable pageable) {
        if (teamId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do team é obrigatório para filtrar goleadas");
        }
        return matchRepository.findLandslideByTeam(teamId, pageable);
    }

    public Page<Match> listClashes(Long team1Id, Long team2Id, Boolean landslides, Pageable pageable) {
        if (team1Id.equals(team2Id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os IDs dos clubes não podem ser os mesmos para um confronto.");
        }

        if (landslides != null && landslides) {
            return matchRepository.findLandslideClashes(team1Id, team2Id, pageable);
        }
        return matchRepository.findClashes(team1Id, team2Id, pageable);
    }

    public RetrospectVersusDTO getRetrospectPlays(Long team1Id, Long team2Id) {
        if (team1Id.equals(team2Id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os IDs dos clubes não podem ser os mesmos para obter o retrospecto de confronto.");
        }

        Team team1 = findTeamById(team1Id, "Clube 1 não encontrado");
        Team team2 = findTeamById(team2Id, "Clube 2 não encontrado");

        List<Object[]> results = matchRepository.calculateClashesRetrospect(team1Id, team2Id);

        if (results.isEmpty()) {
            return new RetrospectVersusDTO(team1, team2, 0, 0, 0, 0, 0, 0, 0);
        }

        Object[] stats = results.get(0);
        return new RetrospectVersusDTO(
                team1,
                team2,
                ((Number) stats[0]).intValue(),    // totalPlays
                ((Number) stats[1]).intValue(),    // winsTeam1
                ((Number) stats[2]).intValue(),    // tiedPlays
                ((Number) stats[3]).intValue(),    // winsTeam2
                ((Number) stats[4]).intValue(),    // goalsTeam1
                ((Number) stats[5]).intValue(),    // goalsTeam2
                ((Number) stats[6]).intValue()     // goalsBalance
        );
    }

    public List<RankingDTO> getRanking(String criteria) {
        switch (criteria) {
            case "jogos":
                return matchRepository.rankingByPlays();
            case "victories":
                return matchRepository.rankingByVictories();
            case "gols":
                return matchRepository.rankingByGoals();
            case "pontos":
                return matchRepository.rankingByPoints();
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Critério de ranking inválido");
        }
    }
}