package com.meli.api_futebol.service;

import com.meli.api_futebol.dto.RetrospectVersusDTO;
import com.meli.api_futebol.dto.TeamDTO;
import com.meli.api_futebol.dto.TeamRetrospectDTO;
import com.meli.api_futebol.model.Team;
import com.meli.api_futebol.repository.MatchRepository;
import com.meli.api_futebol.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private TeamService teamService;

    private Team team;
    private TeamDTO teamDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setTeamId(1L);
        team.setTeamName("Flamengo");
        team.setTeamState("RJ");
        team.setCreationDate(LocalDate.of(1895, 11, 17));
        team.setActive(true);

        teamDTO = new TeamDTO(
                "Flamengo",
                "RJ",
                LocalDate.of(1895, 11, 17)
        );

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve registrar um novo time com sucesso")
    void shouldRegistryTeamSuccessfully() {
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        Team savedTeam = teamService.registryTeam(teamDTO);

        assertNotNull(savedTeam);
        assertEquals(team.getTeamName(), savedTeam.getTeamName());
        assertEquals(team.getTeamState(), savedTeam.getTeamState());
        assertEquals(team.getCreationDate(), savedTeam.getCreationDate());
        assertTrue(savedTeam.isActive());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    @DisplayName("Deve atualizar um time existente com sucesso")
    void shouldUpdateTeamSuccessfully() {
        Team updatedTeam = new Team();
        updatedTeam.setTeamId(1L);
        updatedTeam.setTeamName("Flamengo Atualizado");
        updatedTeam.setTeamState("MG");
        updatedTeam.setCreationDate(LocalDate.of(1900, 1, 1));
        updatedTeam.setActive(true); // Should remain true or be explicitly set

        TeamDTO updatedTeamDTO = new TeamDTO(
                "Flamengo Atualizado",
                "MG",
                LocalDate.of(1900, 1, 1)
        );

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(updatedTeam);

        Team result = teamService.teamUpdate(1L, updatedTeamDTO);

        assertNotNull(result);
        assertEquals(updatedTeam.getTeamName(), result.getTeamName());
        assertEquals(updatedTeam.getTeamState(), result.getTeamState());
        assertEquals(updatedTeam.getCreationDate(), result.getCreationDate());
        verify(teamRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar time não encontrado")
    void shouldThrowExceptionWhenUpdateTeamNotFound() {
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                teamService.teamUpdate(99L, teamDTO));

        assertEquals("404 NOT_FOUND \"Clube não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(99L);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Deve desativar um time com sucesso")
    void shouldDeactivateTeamSuccessfully() {
        Team activeTeam = new Team();
        activeTeam.setTeamId(1L);
        activeTeam.setActive(true);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(activeTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(activeTeam); // The saved team should now be inactive

        teamService.teamDeactivate(1L);

        assertFalse(activeTeam.isActive()); // Verify the team object itself is set to inactive
        verify(teamRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).save(activeTeam);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar desativar time não encontrado")
    void shouldThrowExceptionWhenDeactivateTeamNotFound() {
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                teamService.teamDeactivate(99L));

        assertEquals("404 NOT_FOUND \"Clube não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(99L);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Deve encontrar um time por ID com sucesso")
    void shouldFindTeamByIdSuccessfully() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        Team foundTeam = teamService.findTeamById(1L);

        assertNotNull(foundTeam);
        assertEquals(team.getTeamId(), foundTeam.getTeamId());
        verify(teamRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar time por ID")
    void shouldThrowExceptionWhenFindTeamByIdNotFound() {
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                teamService.findTeamById(99L));

        assertEquals("404 NOT_FOUND \"Clube não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar times sem filtros")
    void shouldListAllTeamsWhenNoFilters() {
        Page<Team> teamPage = new PageImpl<>(Collections.singletonList(team));
        when(teamRepository.findAll(pageable)).thenReturn(teamPage);

        Page<Team> result = teamService.listTeam(null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(team.getTeamName(), result.getContent().get(0).getTeamName());
        verify(teamRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar times filtrando por nome")
    void shouldListTeamsFilteredByName() {
        Page<Team> teamPage = new PageImpl<>(Collections.singletonList(team));
        when(teamRepository.findByNameContainingIgnoreCase("Flamengo", pageable)).thenReturn(teamPage);

        Page<Team> result = teamService.listTeam("Flamengo", null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(teamRepository, times(1)).findByNameContainingIgnoreCase("Flamengo", pageable);
    }

    @Test
    @DisplayName("Deve listar times filtrando por estado")
    void shouldListTeamsFilteredByState() {
        Page<Team> teamPage = new PageImpl<>(Collections.singletonList(team));
        when(teamRepository.findByState("RJ", pageable)).thenReturn(teamPage);

        Page<Team> result = teamService.listTeam(null, "RJ", null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(teamRepository, times(1)).findByState("RJ", pageable);
    }

    @Test
    @DisplayName("Deve listar times filtrando por status ativo")
    void shouldListTeamsFilteredByActive() {
        Page<Team> teamPage = new PageImpl<>(Collections.singletonList(team));
        when(teamRepository.findByActive(true, pageable)).thenReturn(teamPage);

        Page<Team> result = teamService.listTeam(null, null, true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(teamRepository, times(1)).findByActive(true, pageable);
    }

    @Test
    @DisplayName("Deve listar times filtrando por nome e estado")
    void shouldListTeamsFilteredByNameAndState() {
        Page<Team> teamPage = new PageImpl<>(Collections.singletonList(team));
        when(teamRepository.findByNameContainingIgnoreCaseAndState("Flamengo", "RJ", pageable)).thenReturn(teamPage);

        Page<Team> result = teamService.listTeam("Flamengo", "RJ", null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(teamRepository, times(1)).findByNameContainingIgnoreCaseAndState("Flamengo", "RJ", pageable);
    }

    @Test
    @DisplayName("Deve listar times filtrando por nome e ativo")
    void shouldListTeamsFilteredByNameAndActive() {
        Page<Team> teamPage = new PageImpl<>(Collections.singletonList(team));
        when(teamRepository.findByNameContainingIgnoreCaseAndActive("Flamengo", true, pageable)).thenReturn(teamPage);

        Page<Team> result = teamService.listTeam("Flamengo", null, true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(teamRepository, times(1)).findByNameContainingIgnoreCaseAndActive("Flamengo", true, pageable);
    }

    @Test
    @DisplayName("Deve listar times filtrando por estado e ativo")
    void shouldListTeamsFilteredByStateAndActive() {
        Page<Team> teamPage = new PageImpl<>(Collections.singletonList(team));
        when(teamRepository.findByStateAndActive("RJ", true, pageable)).thenReturn(teamPage);

        Page<Team> result = teamService.listTeam(null, "RJ", true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(teamRepository, times(1)).findByStateAndActive("RJ", true, pageable);
    }

    @Test
    @DisplayName("Deve listar times filtrando por nome, estado e ativo")
    void shouldListTeamsFilteredByNameStateAndActive() {
        Page<Team> teamPage = new PageImpl<>(Collections.singletonList(team));
        when(teamRepository.findByNameContainingIgnoreCaseAndStateAndActive("Flamengo", "RJ", true, pageable)).thenReturn(teamPage);

        Page<Team> result = teamService.listTeam("Flamengo", "RJ", true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(teamRepository, times(1)).findByNameContainingIgnoreCaseAndStateAndActive("Flamengo", "RJ", true, pageable);
    }

    @Test
    @DisplayName("Deve retornar retrospecto do time com sucesso")
    void shouldReturnTeamRetrospectSuccessfully() {
        Object[] stats = {10, 5, 3, 2, 20, 10}; // totalPlays, victories, ties, loses, goalsPro, goalsCon
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(matchRepository.calculateRetrospect(1L)).thenReturn(Collections.singletonList(stats));

        TeamRetrospectDTO retrospect = teamService.getRetrospect(1L);

        assertNotNull(retrospect);
        assertEquals(team, retrospect.team());
        assertEquals(10, retrospect.totalPlays());
        assertEquals(5, retrospect.victories());
        assertEquals(3, retrospect.ties());
        assertEquals(2, retrospect.loses());
        assertEquals(20, retrospect.goalsPro());
        assertEquals(10, retrospect.goalsCon());
        verify(teamRepository, times(1)).findById(1L);
        verify(matchRepository, times(1)).calculateRetrospect(1L);
    }

    @Test
    @DisplayName("Deve retornar retrospecto do time com zero quando não houver partidas")
    void shouldReturnZeroRetrospectWhenNoMatches() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(matchRepository.calculateRetrospect(1L)).thenReturn(Collections.emptyList());

        TeamRetrospectDTO retrospect = teamService.getRetrospect(1L);

        assertNotNull(retrospect);
        assertEquals(team, retrospect.team());
        assertEquals(0, retrospect.totalPlays());
        assertEquals(0, retrospect.victories());
        assertEquals(0, retrospect.ties());
        assertEquals(0, retrospect.loses());
        assertEquals(0, retrospect.goalsPro());
        assertEquals(0, retrospect.goalsCon());
        verify(teamRepository, times(1)).findById(1L);
        verify(matchRepository, times(1)).calculateRetrospect(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar obter retrospecto de time não encontrado")
    void shouldThrowExceptionWhenGetRetrospectTeamNotFound() {
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                teamService.getRetrospect(99L));

        assertEquals("404 NOT_FOUND \"Clube não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(99L);
        verify(matchRepository, never()).calculateRetrospect(anyLong());
    }

    @Test
    @DisplayName("Deve retornar retrospecto contra adversários com sucesso")
    void shouldReturnRetrospectVersusRivalsSuccessfully() {
        Team rivalTeam = new Team();
        rivalTeam.setTeamId(2L);
        rivalTeam.setTeamName("Palmeiras");

        RetrospectVersusDTO retrospectDTO = new RetrospectVersusDTO(
                team, rivalTeam, 5, 2, 1, 2, 8, 7, 1
        );
        List<RetrospectVersusDTO> retrospectList = Collections.singletonList(retrospectDTO);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(matchRepository.calculateRetrospectVersusRivals(1L)).thenReturn(retrospectList);

        List<RetrospectVersusDTO> result = teamService.getRetrospectVersusRivals(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(retrospectDTO, result.get(0));
        verify(teamRepository, times(1)).findById(1L);
        verify(matchRepository, times(1)).calculateRetrospectVersusRivals(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia de retrospecto contra adversários quando não houver partidas")
    void shouldReturnEmptyListWhenNoRetrospectVersusRivals() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(matchRepository.calculateRetrospectVersusRivals(1L)).thenReturn(Collections.emptyList());

        List<RetrospectVersusDTO> result = teamService.getRetrospectVersusRivals(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(teamRepository, times(1)).findById(1L);
        verify(matchRepository, times(1)).calculateRetrospectVersusRivals(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar obter retrospecto contra adversários de time não encontrado")
    void shouldThrowExceptionWhenGetRetrospectVersusRivalsTeamNotFound() {
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                teamService.getRetrospectVersusRivals(99L));

        assertEquals("404 NOT_FOUND \"Clube não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(99L);
        verify(matchRepository, never()).calculateRetrospectVersusRivals(anyLong());
    }
}