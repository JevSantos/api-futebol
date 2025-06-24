package com.meli.api_futebol.service;

import com.meli.api_futebol.dto.TeamDTO;
import com.meli.api_futebol.dto.TeamRetrospectDTO;
import com.meli.api_futebol.dto.RetrospectVersusDTO;
import com.meli.api_futebol.model.Team;
import com.meli.api_futebol.repository.TeamRepository;
import com.meli.api_futebol.repository.PartidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final PartidaRepository partidaRepository;

    public Team registryTeam(TeamDTO dto) {
        Team team = new Team();
        team.setTeamName(dto.teamName());
        team.setTeamState(dto.teamState());
        team.setCreationDate(dto.creationDate() != null ? dto.creationDate() : LocalDate.now());
        team.setActive(true);
        return teamRepository.save(team);
    }

    public Team teamUpdate(Long id, TeamDTO dto) {
        Team team = findTeamById(id);
        team.setTeamName(dto.teamName());
        team.setTeamState(dto.teamState());
        team.setCreationDate(dto.creationDate());
        return teamRepository.save(team);
    }

    public void teamDeactivate(Long id) {
        Team team = findTeamById(id);
        team.setActive(false);
        teamRepository.save(team);
    }

    public Team findTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube não encontrado"));
    }

    public Page<Team> listTeam(String name, String state, Boolean active, Pageable pageable) {
        if (name != null && state != null && active != null) {
            return teamRepository.findByNameContainingIgnoreCaseAndStateAndActive(name, state, active, pageable);
        } else if (name != null && state != null) {
            return teamRepository.findByNameContainingIgnoreCaseAndState(name, state, pageable);
        } else if (name != null && active != null) {
            return teamRepository.findByNameContainingIgnoreCaseAndActive(name, active, pageable);
        } else if (state != null && active != null) {
            return teamRepository.findByStateAndActive(state, active, pageable);
        } else if (name != null) {
            return teamRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (state != null) {
            return teamRepository.findByState(state, pageable);
        } else if (active != null) {
            return teamRepository.findByActive(active, pageable);
        }
        return teamRepository.findAll(pageable);
    }


    public TeamRetrospectDTO getRetrospect(Long clubeId) {
        Team team = findTeamById(clubeId);

        List<Object[]> scoreboards = partidaRepository.calcularRetrospecto(clubeId);
        if (scoreboards.isEmpty()) {
            return new TeamRetrospectDTO(team, 0, 0, 0, 0, 0, 0);
        }

        Object[] statistics = scoreboards.get(0);
        return new TeamRetrospectDTO(
                team,
                ((Number) statistics[0]).intValue(),    // Jogos
                ((Number) statistics[1]).intValue(),    // vitorias
                ((Number) statistics[2]).intValue(),    // empates
                ((Number) statistics[3]).intValue(),    // derrotas
                ((Number) statistics[4]).intValue(),    // golsFeitos
                ((Number) statistics[5]).intValue()     // golsSofridos
        );
    }

    public List<RetrospectVersusDTO> getRetrospectVersusRivals(Long teamId) {
        findTeamById(teamId); // Verifica se o clube existe
        return partidaRepository.calculateRetrospectVersusRivals(teamId);
    }
}