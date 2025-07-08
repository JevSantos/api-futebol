package com.meli.api_futebol.service;

import com.meli.api_futebol.dto.MatchDTO;
import com.meli.api_futebol.dto.RankingDTO;
import com.meli.api_futebol.dto.RetrospectVersusDTO;
import com.meli.api_futebol.model.Match;
import com.meli.api_futebol.model.Stadium;
import com.meli.api_futebol.model.Team;
import com.meli.api_futebol.repository.MatchRepository;
import com.meli.api_futebol.repository.StadiumRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private StadiumRepository stadiumRepository;

    @InjectMocks
    private MatchService matchService;

    private Team homeTeam;
    private Team awayTeam;
    private Stadium stadium;
    private MatchDTO matchDTO;
    private Match match;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        homeTeam = new Team();
        homeTeam.setTeamId(1L);
        homeTeam.setTeamName("Flamengo");

        awayTeam = new Team();
        awayTeam.setTeamId(2L);
        awayTeam.setTeamName("Palmeiras");

        stadium = new Stadium();
        stadium.setStadiumId(10L);
        stadium.setStadiumName("Maracanã");

        matchDTO = new MatchDTO(
                homeTeam.getTeamId(),
                awayTeam.getTeamId(),
                2,
                1,
                stadium.getStadiumId(),
                LocalDateTime.now()
        );

        match = new Match();
        match.setMatchId(1L);
        match.setHomeTeamId(homeTeam);
        match.setAwayTeamId(awayTeam);
        match.setGoalsHomeTeam(2);
        match.setGoalsAwayTeam(1);
        match.setStadiumId(stadium);
        match.setMatchDateTime(LocalDateTime.now());

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar uma partida com sucesso")
    void shouldCreateMatchSuccessfully() {
        when(teamRepository.findById(homeTeam.getTeamId())).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(awayTeam.getTeamId())).thenReturn(Optional.of(awayTeam));
        when(stadiumRepository.findById(stadium.getStadiumId())).thenReturn(Optional.of(stadium));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        Match createdMatch = matchService.createMatch(matchDTO);

        assertNotNull(createdMatch);
        assertEquals(match.getHomeTeamId().getTeamId(), createdMatch.getHomeTeamId().getTeamId());
        assertEquals(match.getAwayTeamId().getTeamId(), createdMatch.getAwayTeamId().getTeamId());
        assertEquals(match.getStadiumId().getStadiumId(), createdMatch.getStadiumId().getStadiumId());
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar partida com time mandante não encontrado")
    void shouldThrowExceptionWhenCreateMatchHomeTeamNotFound() {
        when(teamRepository.findById(homeTeam.getTeamId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.createMatch(matchDTO));

        assertEquals("404 NOT_FOUND \"Clube mandante não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(homeTeam.getTeamId());
        verify(teamRepository, never()).findById(awayTeam.getTeamId());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar partida com time visitante não encontrado")
    void shouldThrowExceptionWhenCreateMatchAwayTeamNotFound() {
        when(teamRepository.findById(homeTeam.getTeamId())).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(awayTeam.getTeamId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.createMatch(matchDTO));

        assertEquals("404 NOT_FOUND \"Clube visitante não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(homeTeam.getTeamId());
        verify(teamRepository, times(1)).findById(awayTeam.getTeamId());
        verify(stadiumRepository, never()).findById(anyLong());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar partida com estádio não encontrado")
    void shouldThrowExceptionWhenCreateMatchStadiumNotFound() {
        when(teamRepository.findById(homeTeam.getTeamId())).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(awayTeam.getTeamId())).thenReturn(Optional.of(awayTeam));
        when(stadiumRepository.findById(stadium.getStadiumId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.createMatch(matchDTO));

        assertEquals("404 NOT_FOUND \"Estádio não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(homeTeam.getTeamId());
        verify(teamRepository, times(1)).findById(awayTeam.getTeamId());
        verify(stadiumRepository, times(1)).findById(stadium.getStadiumId());
        verify(matchRepository, never()).save(any(Match.class));
    }

/*    @Test
    @DisplayName("Deve lançar exceção ao criar partida com times iguais")
    void shouldThrowExceptionWhenCreateMatchWithSameTeams() {
        matchDTO = new MatchDTO(homeTeam.getTeamId(), awayTeam.getTeamId(), 2, 1, stadium.getStadiumId(), LocalDateTime.now());

        when(teamRepository.findById(homeTeam.getTeamId())).thenReturn(Optional.of(awayTeam));
        // Only one findById for the same team ID
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.createMatch(matchDTO));

        assertEquals("400 BAD_REQUEST \"O team mandante e o team visitante não podem ser o mesmo.\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(homeTeam.getTeamId()); // Called for home team
        verify(teamRepository, times(1)).findById(awayTeam.getTeamId()); // Called for away team (same ID)
        verify(stadiumRepository, never()).findById(anyLong());
        verify(matchRepository, never()).save(any(Match.class));
    }*/

    @Test
    @DisplayName("Deve atualizar uma partida com sucesso")
    void shouldUpdateMatchSuccessfully() {
        MatchDTO updateDTO = new MatchDTO(
                3L, // New home team ID
                null,
                3,
                null,
                null,
                null
        );
        Team newHomeTeam = new Team();
        newHomeTeam.setTeamId(3L);
        newHomeTeam.setTeamName("Cruzeiro");

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(teamRepository.findById(3L)).thenReturn(Optional.of(newHomeTeam));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        Match updatedMatch = matchService.updateMatch(1L, updateDTO);

        assertNotNull(updatedMatch);
        assertEquals(3L, updatedMatch.getHomeTeamId().getTeamId());
        assertEquals(3, updatedMatch.getGoalsHomeTeam());
        verify(matchRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(3L);
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar partida com novo time mandante igual ao visitante")
    void shouldThrowExceptionWhenUpdateMatchHomeTeamEqualsAwayTeam() {
        MatchDTO updateDTO = new MatchDTO(
                2L, // Try to set home team to away team's ID
                null, null, null, null, null
        );

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam)); // Mock the new home team (which is the away team)

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.updateMatch(1L, updateDTO));

        assertEquals("400 BAD_REQUEST \"O novo team mandante não pode ser igual ao team visitante atual.\"", exception.getMessage());
        verify(matchRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(2L);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @DisplayName("Deve remover uma partida com sucesso")
    void shouldRemoveMatchSuccessfully() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        doNothing().when(matchRepository).delete(match);

        matchService.removeMatch(1L);

        verify(matchRepository, times(1)).findById(1L);
        verify(matchRepository, times(1)).delete(match);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover partida não encontrada")
    void shouldThrowExceptionWhenRemoveMatchNotFound() {
        when(matchRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.removeMatch(99L));

        assertEquals("404 NOT_FOUND \"Partida não encontrada\"", exception.getMessage());
        verify(matchRepository, times(1)).findById(99L);
        verify(matchRepository, never()).delete(any(Match.class));
    }

    @Test
    @DisplayName("Deve encontrar uma partida por ID com sucesso")
    void shouldFindMatchByIdSuccessfully() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        Match foundMatch = matchService.findMatchById(1L);

        assertNotNull(foundMatch);
        assertEquals(match.getMatchId(), foundMatch.getMatchId());
        verify(matchRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar partida por ID")
    void shouldThrowExceptionWhenFindMatchByIdNotFound() {
        when(matchRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.findMatchById(99L));

        assertEquals("404 NOT_FOUND \"Partida não encontrada\"", exception.getMessage());
        verify(matchRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar partidas sem filtros")
    void shouldListAllMatchesWhenNoFilters() {
        Page<Match> matchPage = new PageImpl<>(Collections.singletonList(match));
        when(matchRepository.findAll(pageable)).thenReturn(matchPage);

        Page<Match> result = matchService.listMatch(null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(matchRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar partidas filtrando por ID do time")
    void shouldListMatchesFilteredByTeamId() {
        Page<Match> matchPage = new PageImpl<>(Collections.singletonList(match));
        when(matchRepository.findByHomeTeamIdOrAwayTeamId(homeTeam.getTeamId(), homeTeam.getTeamId(), pageable)).thenReturn(matchPage);

        Page<Match> result = matchService.listMatch(homeTeam.getTeamId(), null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(matchRepository, times(1)).findByHomeTeamIdOrAwayTeamId(homeTeam.getTeamId(), homeTeam.getTeamId(), pageable);
    }

    @Test
    @DisplayName("Deve listar partidas filtrando por ID do estádio")
    void shouldListMatchesFilteredByStadiumId() {
        Page<Match> matchPage = new PageImpl<>(Collections.singletonList(match));
        when(matchRepository.findByStadiumId(stadium.getStadiumId(), pageable)).thenReturn(matchPage);

        Page<Match> result = matchService.listMatch(null, stadium.getStadiumId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(matchRepository, times(1)).findByStadiumId(stadium.getStadiumId(), pageable);
    }

    @Test
    @DisplayName("Deve listar partidas filtrando por ID do time e ID do estádio")
    void shouldListMatchesFilteredByTeamAndStadiumId() {
        Page<Match> matchPage = new PageImpl<>(Collections.singletonList(match));
        when(matchRepository.findByHomeTeamIdOrAwayTeamIdAndStadiumId(homeTeam.getTeamId(), stadium.getStadiumId(), pageable)).thenReturn(matchPage);

        Page<Match> result = matchService.listMatch(homeTeam.getTeamId(), stadium.getStadiumId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(matchRepository, times(1)).findByHomeTeamIdOrAwayTeamIdAndStadiumId(homeTeam.getTeamId(), stadium.getStadiumId(), pageable);
    }

    @Test
    @DisplayName("Deve listar goleadas para um time com sucesso")
    void shouldListLandslidesForTeamSuccessfully() {
        Page<Match> landslidePage = new PageImpl<>(Collections.singletonList(match)); // Assume match is a landslide
        when(matchRepository.findLandslideByTeam(homeTeam.getTeamId(), pageable)).thenReturn(landslidePage);

        Page<Match> result = matchService.listLandslides(homeTeam.getTeamId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(matchRepository, times(1)).findLandslideByTeam(homeTeam.getTeamId(), pageable);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar goleadas sem ID do time")
    void shouldThrowExceptionWhenListLandslidesWithoutTeamId() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.listLandslides(null, pageable));

        assertEquals("400 BAD_REQUEST \"ID do team é obrigatório para filtrar goleadas\"", exception.getMessage());
        verify(matchRepository, never()).findLandslideByTeam(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar confrontos entre dois times com sucesso")
    void shouldListClashesSuccessfully() {
        Page<Match> clashPage = new PageImpl<>(Collections.singletonList(match));
        when(matchRepository.findClashes(homeTeam.getTeamId(), awayTeam.getTeamId(), pageable)).thenReturn(clashPage);

        Page<Match> result = matchService.listClashes(homeTeam.getTeamId(), awayTeam.getTeamId(), false, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(matchRepository, times(1)).findClashes(homeTeam.getTeamId(), awayTeam.getTeamId(), pageable);
        verify(matchRepository, never()).findLandslideClashes(anyLong(), anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar goleadas entre dois times com sucesso")
    void shouldListLandslideClashesSuccessfully() {
        Page<Match> landslideClashPage = new PageImpl<>(Collections.singletonList(match));
        when(matchRepository.findLandslideClashes(homeTeam.getTeamId(), awayTeam.getTeamId(), pageable)).thenReturn(landslideClashPage);

        Page<Match> result = matchService.listClashes(homeTeam.getTeamId(), awayTeam.getTeamId(), true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(matchRepository, times(1)).findLandslideClashes(homeTeam.getTeamId(), awayTeam.getTeamId(), pageable);
        verify(matchRepository, never()).findClashes(anyLong(), anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar confrontos com times iguais")
    void shouldThrowExceptionWhenListClashesWithSameTeams() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.listClashes(homeTeam.getTeamId(), homeTeam.getTeamId(), false, pageable));

        assertEquals("400 BAD_REQUEST \"Os IDs dos clubes não podem ser os mesmos para um confronto.\"", exception.getMessage());
        verify(matchRepository, never()).findClashes(anyLong(), anyLong(), any(Pageable.class));
        verify(matchRepository, never()).findLandslideClashes(anyLong(), anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar retrospecto de confronto entre dois times com sucesso")
    void shouldReturnClashesRetrospectSuccessfully() {
        Object[] stats = {5, 2, 1, 2, 8, 7, 1}; // totalJogos, vitoriasTeam1, empates, vitoriasTeam2, goalsTeam1, goalsTeam2, goalsBalance
        List<Object[]> results = Collections.singletonList(stats);

        when(teamRepository.findById(homeTeam.getTeamId())).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(awayTeam.getTeamId())).thenReturn(Optional.of(awayTeam));
        when(matchRepository.calculateClashesRetrospect(homeTeam.getTeamId(), awayTeam.getTeamId())).thenReturn(results);

        RetrospectVersusDTO retrospect = matchService.getRetrospectPlays(homeTeam.getTeamId(), awayTeam.getTeamId());

        assertNotNull(retrospect);
        assertEquals(homeTeam, retrospect.homeTeam());
        assertEquals(awayTeam, retrospect.awayTeam());
        assertEquals(5, retrospect.totalPlays());
        assertEquals(2, retrospect.homeTeamVictories());
        assertEquals(1, retrospect.drawsQtd());
        assertEquals(2, retrospect.awayTeamVictories());
        assertEquals(8, retrospect.goalsHomeTeam());
        assertEquals(7, retrospect.goalsAwayTeam());
        assertEquals(1, retrospect.goalsBalance());

        verify(teamRepository, times(1)).findById(homeTeam.getTeamId());
        verify(teamRepository, times(1)).findById(awayTeam.getTeamId());
        verify(matchRepository, times(1)).calculateClashesRetrospect(homeTeam.getTeamId(), awayTeam.getTeamId());
    }

    @Test
    @DisplayName("Deve retornar retrospecto de confronto com zero quando não houver partidas")
    void shouldReturnZeroClashesRetrospectWhenNoMatches() {
        when(teamRepository.findById(homeTeam.getTeamId())).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(awayTeam.getTeamId())).thenReturn(Optional.of(awayTeam));
        when(matchRepository.calculateClashesRetrospect(homeTeam.getTeamId(), awayTeam.getTeamId())).thenReturn(Collections.emptyList());

        RetrospectVersusDTO retrospect = matchService.getRetrospectPlays(homeTeam.getTeamId(), awayTeam.getTeamId());

        assertNotNull(retrospect);
        assertEquals(homeTeam, retrospect.homeTeam());
        assertEquals(awayTeam, retrospect.awayTeam());
        assertEquals(0, retrospect.totalPlays());
        assertEquals(0, retrospect.homeTeamVictories());
        assertEquals(0, retrospect.drawsQtd());
        assertEquals(0, retrospect.awayTeamVictories());
        assertEquals(0, retrospect.goalsHomeTeam());
        assertEquals(0, retrospect.goalsAwayTeam());
        assertEquals(0, retrospect.goalsBalance());

        verify(teamRepository, times(1)).findById(homeTeam.getTeamId());
        verify(teamRepository, times(1)).findById(awayTeam.getTeamId());
        verify(matchRepository, times(1)).calculateClashesRetrospect(homeTeam.getTeamId(), awayTeam.getTeamId());
    }


    @Test
    @DisplayName("Deve lançar exceção ao obter retrospecto de confronto com times iguais")
    void shouldThrowExceptionWhenGetClashesRetrospectWithSameTeams() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.getRetrospectPlays(homeTeam.getTeamId(), homeTeam.getTeamId()));

        assertEquals("400 BAD_REQUEST \"Os IDs dos clubes não podem ser os mesmos para obter o retrospecto de confronto.\"", exception.getMessage());
        verify(teamRepository, never()).findById(anyLong());
        verify(matchRepository, never()).calculateClashesRetrospect(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Deve retornar ranking por número de jogos")
    void shouldReturnRankingByPlays() {
        RankingDTO rankingDTO = new RankingDTO(homeTeam, 10L);
        when(matchRepository.rankingByPlays()).thenReturn(Collections.singletonList(rankingDTO));

        List<RankingDTO> ranking = matchService.getRanking("jogos");

        assertNotNull(ranking);
        assertFalse(ranking.isEmpty());
        assertEquals(1, ranking.size());
        assertEquals(rankingDTO, ranking.get(0));
        verify(matchRepository, times(1)).rankingByPlays();
    }

    @Test
    @DisplayName("Deve retornar ranking por vitórias")
    void shouldReturnRankingByVictories() {
        RankingDTO rankingDTO = new RankingDTO(homeTeam, 5L);
        when(matchRepository.rankingByVictories()).thenReturn(Collections.singletonList(rankingDTO));

        List<RankingDTO> ranking = matchService.getRanking("victories");

        assertNotNull(ranking);
        assertFalse(ranking.isEmpty());
        assertEquals(1, ranking.size());
        assertEquals(rankingDTO, ranking.get(0));
        verify(matchRepository, times(1)).rankingByVictories();
    }

    @Test
    @DisplayName("Deve retornar ranking por gols")
    void shouldReturnRankingByGoals() {
        RankingDTO rankingDTO = new RankingDTO(homeTeam, 20L);
        when(matchRepository.rankingByGoals()).thenReturn(Collections.singletonList(rankingDTO));

        List<RankingDTO> ranking = matchService.getRanking("gols");

        assertNotNull(ranking);
        assertFalse(ranking.isEmpty());
        assertEquals(1, ranking.size());
        assertEquals(rankingDTO, ranking.get(0));
        verify(matchRepository, times(1)).rankingByGoals();
    }

    @Test
    @DisplayName("Deve retornar ranking por pontos")
    void shouldReturnRankingByPoints() {
        RankingDTO rankingDTO = new RankingDTO(homeTeam, 16L); // 5 victories * 3 + 1 tie * 1 = 16
        when(matchRepository.rankingByPoints()).thenReturn(Collections.singletonList(rankingDTO));

        List<RankingDTO> ranking = matchService.getRanking("pontos");

        assertNotNull(ranking);
        assertFalse(ranking.isEmpty());
        assertEquals(1, ranking.size());
        assertEquals(rankingDTO, ranking.get(0));
        verify(matchRepository, times(1)).rankingByPoints();
    }

    @Test
    @DisplayName("Deve lançar exceção para critério de ranking inválido")
    void shouldThrowExceptionForInvalidRankingCriteria() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchService.getRanking("invalido"));

        assertEquals("400 BAD_REQUEST \"Critério de ranking inválido\"", exception.getMessage());
        verify(matchRepository, never()).rankingByPlays();
        verify(matchRepository, never()).rankingByVictories();
        verify(matchRepository, never()).rankingByGoals();
        verify(matchRepository, never()).rankingByPoints();
    }
}