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
public class SoccerMatchService {

    private final SoccerMatchRepository soccerMatchRepository;
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

    public SoccerMatch createMatch(SoccerMatchDTO dto) {
        SoccerMatch soccermatch = new SoccerMatch();

        Team homeTeam = findTeamById(dto.homeTeamId(), "Clube mandante não encontrado");
        Team awayTeam = findTeamById(dto.awayTeamId(), "Clube visitante não encontrado");
        Stadium stadium = findStadiumById(dto.stadiumId(), "Estádio não encontrado");
        if (homeTeam.getId().equals(awayTeam.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O team mandante e o team visitante não podem ser o mesmo.");
        }

        soccermatch.setHomeTeamId(homeTeam);
        soccermatch.setAwayTeamId(awayTeam);
        soccermatch.setGoalsHomeTeam(dto.goalsHomeTeam());
        soccermatch.setGoalsAwayTeam(dto.goalsAwayTeam());
        soccermatch.setStadiumId(stadium);
        soccermatch.setMatchDateTime(dto.matchDateTime() != null ? dto.matchDateTime() : LocalDateTime.now());

        return soccerMatchRepository.save(soccermatch);
    }

    public SoccerMatch updateMatch(Long id, SoccerMatchDTO dto) {
        SoccerMatch soccermatch = findMatchById(id);

        if (dto.homeTeamId() != null) {
            Team newHomeTeam = findTeamById(dto.homeTeamId(), "Clube mandante não encontrado");
            if (newHomeTeam.getId().equals(soccermatch.getAwayTeamId().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O novo team mandante não pode ser igual ao team visitante atual.");
            }
            soccermatch.setHomeTeamId(newHomeTeam);
        }
        if (dto.awayTeamId() != null) {
            Team newAwayTeam = findTeamById(dto.awayTeamId(), "Clube visitante não encontrado");
            if (newAwayTeam.getId().equals(soccermatch.getHomeTeamId().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O novo team visitante não pode ser igual ao team mandante atual.");
            }
            soccermatch.setAwayTeamId(newAwayTeam);
        }
        if (dto.stadiumId() != null) {
            Stadium stadium = findStadiumById(dto.stadiumId(), "Estádio não encontrado");
            soccermatch.setStadiumId(stadium);
        }
        if (dto.goalsHomeTeam() != null) soccermatch.setGoalsHomeTeam(dto.goalsHomeTeam());
        if (dto.goalsAwayTeam() != null) soccermatch.setGoalsAwayTeam(dto.goalsAwayTeam());
        if (dto.matchDateTime() != null) soccermatch.setMatchDateTime(dto.matchDateTime());

        return soccerMatchRepository.save(soccermatch);
    }

    public void removeMatch(Long id) {
        SoccerMatch soccermatch = findMatchById(id);
        soccerMatchRepository.delete(soccermatch);
    }

    public SoccerMatch findMatchById(Long id) {
        return soccerMatchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada"));
    }

    public Page<SoccerMatch> listMatch(Long teamId, Long stadiumId, Pageable pageable) {
        if (teamId != null && stadiumId != null) {
            return soccerMatchRepository.findByHomeTeamIdOrAwayTeamIdAndStadiumId(teamId, stadiumId, pageable);
        } else if (teamId != null) {
            return soccerMatchRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId, pageable);
        } else if (stadiumId != null) {
            return soccerMatchRepository.findByStadiumId(stadiumId, pageable);
        }
        return soccerMatchRepository.findAll(pageable);
    }

    public Page<SoccerMatch> listLandslides(Long teamId, Pageable pageable) {
        if (teamId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do team é obrigatório para filtrar goleadas");
        }
        return soccerMatchRepository.findLandslideByTeam(teamId, pageable);
    }

    public Page<SoccerMatch> listClashes(Long team1Id, Long team2Id, Boolean landslides, Pageable pageable) {
        if (team1Id.equals(team2Id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os IDs dos clubes não podem ser os mesmos para um confronto.");
        }

        if (landslides != null && landslides) {
            return soccerMatchRepository.findLandslideClashes(team1Id, team2Id, pageable);
        }
        return soccerMatchRepository.findClashes(team1Id, team2Id, pageable);
    }

    public RetrospectVersusDTO getRetrospectPlays(Long team1Id, Long team2Id) {
        if (team1Id.equals(team2Id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os IDs dos clubes não podem ser os mesmos para obter o retrospecto de confronto.");
        }

        Team team1 = findTeamById(team1Id, "Clube 1 não encontrado");
        Team team2 = findTeamById(team2Id, "Clube 2 não encontrado");

        List<Object[]> results = soccerMatchRepository.calculateClashesRetrospect(team1Id, team2Id);

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
                return soccerMatchRepository.rankingByPlays();
            case "victories":
                return soccerMatchRepository.rankingByVictories();
            case "gols":
                return soccerMatchRepository.rankingByGoals();
            case "pontos":
                return soccerMatchRepository.rankingByPoints();
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Criterio de classificação inválido");
        }
    }
}