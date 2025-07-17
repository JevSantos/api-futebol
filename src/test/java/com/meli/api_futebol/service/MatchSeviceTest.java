package com.meli.api_futebol.service;

import com.meli.api_futebol.dto.SoccerMatchDTO;
import com.meli.api_futebol.dto.RankingDTO;
import com.meli.api_futebol.dto.RetrospectVersusDTO;
import com.meli.api_futebol.model.SoccerMatch;
import com.meli.api_futebol.model.Stadium;
import com.meli.api_futebol.model.Team;
import com.meli.api_futebol.repository.SoccerMatchRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoccerMatchServiceTest {

    @Mock
    private SoccerMatchRepository soccerMatchRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private StadiumRepository stadiumRepository;

    @InjectMocks
    private SoccerMatchService soccerMatchService;

    private Team homeTeam;
    private Team awayTeam;
    private Stadium stadium;
    private SoccerMatchDTO soccerMatchDTO;
    private SoccerMatch soccermatch;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        homeTeam = new Team();
        homeTeam.setId(1L);
        homeTeam.setTeamName("Flamengo");

        awayTeam = new Team();
        awayTeam.setId(2L);
        awayTeam.setTeamName("Palmeiras");

        stadium = new Stadium();
        stadium.setStadiumId(10L);
        stadium.setStadiumName("Maracanã");

        soccerMatchDTO = new SoccerMatchDTO(
                homeTeam.getId(),
                awayTeam.getId(),
                2,
                1,
                stadium.getStadiumId(),
                LocalDateTime.now()
        );

        soccermatch = new SoccerMatch();
        soccermatch.setSoccerMatchId(1L);
        soccermatch.setHomeTeamId(homeTeam);
        soccermatch.setAwayTeamId(awayTeam);
        soccermatch.setGoalsHomeTeam(2);
        soccermatch.setGoalsAwayTeam(1);
        soccermatch.setStadiumId(stadium);
        soccermatch.setMatchDateTime(LocalDateTime.now());

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar uma partida com sucesso")
    void shouldCreateMatchSuccessfully() {
        when(teamRepository.findById(homeTeam.getId())).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(awayTeam.getId())).thenReturn(Optional.of(awayTeam));
        when(stadiumRepository.findById(stadium.getStadiumId())).thenReturn(Optional.of(stadium));
        when(soccerMatchRepository.save(any(SoccerMatch.class))).thenReturn(soccermatch);

        SoccerMatch createdSoccerMatch = soccerMatchService.createMatch(soccerMatchDTO);

        assertNotNull(createdSoccerMatch);
        assertEquals(soccermatch.getHomeTeamId().getId(), createdSoccerMatch.getHomeTeamId().getId());
        assertEquals(soccermatch.getAwayTeamId().getId(), createdSoccerMatch.getAwayTeamId().getId());
        assertEquals(soccermatch.getStadiumId().getStadiumId(), createdSoccerMatch.getStadiumId().getStadiumId());
        verify(soccerMatchRepository, times(1)).save(any(SoccerMatch.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar partida com time mandante não encontrado")
    void shouldThrowExceptionWhenCreateMatchHomeTeamNotFound() {
        when(teamRepository.findById(homeTeam.getId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                soccerMatchService.createMatch(soccerMatchDTO));

        assertEquals("404 NOT_FOUND \"Clube mandante não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(homeTeam.getId());
        verify(teamRepository, never()).findById(awayTeam.getId());
        verify(soccerMatchRepository, never()).save(any(SoccerMatch.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar partida com time visitante não encontrado")
    void shouldThrowExceptionWhenCreateMatchAwayTeamNotFound() {
        when(teamRepository.findById(homeTeam.getId())).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(awayTeam.getId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                soccerMatchService.createMatch(soccerMatchDTO));

        assertEquals("404 NOT_FOUND \"Clube visitante não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(homeTeam.getId());
        verify(teamRepository, times(1)).findById(awayTeam.getId());
        verify(stadiumRepository, never()).findById(anyLong());
        verify(soccerMatchRepository, never()).save(any(SoccerMatch.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar partida com estádio não encontrado")
    void shouldThrowExceptionWhenCreateMatchStadiumNotFound() {
        when(teamRepository.findById(homeTeam.getId())).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(awayTeam.getId())).thenReturn(Optional.of(awayTeam));
        when(stadiumRepository.findById(stadium.getStadiumId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                soccerMatchService.createMatch(soccerMatchDTO));

        assertEquals("404 NOT_FOUND \"Estádio não encontrado\"", exception.getMessage());
        verify(teamRepository, times(1)).findById(homeTeam.getId());
        verify(teamRepository, times(1)).findById(awayTeam.getId());
        verify(stadiumRepository, times(1)).findById(stadium.getStadiumId());
        verify(soccerMatchRepository, never()).save(any(SoccerMatch.class));
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
        SoccerMatchDTO updateDTO = new SoccerMatchDTO(
                3L, // New home team ID
                null,
                3,
                null,
                null,
                null
        );
        Team newHomeTeam = new Team();
        newHomeTeam.setId(3L);
        newHomeTeam.setTeamName("Cruzeiro");

        when(soccerMatchRepository.findById(1L)).thenReturn(Optional.of(soccermatch));
        when(teamRepository.findById(3L)).thenReturn(Optional.of(newHomeTeam));
        when(soccerMatchRepository.save(any(SoccerMatch.class))).thenReturn(soccermatch);

        SoccerMatch updatedSoccerMatch = soccerMatchService.updateMatch(1L, updateDTO);

        assertNotNull(updatedSoccerMatch);
        assertEquals(3L, updatedSoccerMatch.getHomeTeamId().getId());
        assertEquals(3, updatedSoccerMatch.getGoalsHomeTeam());
        verify(soccerMatchRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(3L);
        verify(soccerMatchRepository, times(1)).save(any(SoccerMatch.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar partida com novo time mandante igual ao visitante")
    void shouldThrowExceptionWhenUpdateMatchHomeTeamEqualsAwayTeam() {
        SoccerMatchDTO updateDTO = new SoccerMatchDTO(
                2L, // Try to set home team to away team's ID
                null, null, null, null, null
        );

        when(soccerMatchRepository.findById(1L)).thenReturn(Optional.of(soccermatch));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam)); // Mock the new home team (which is the away team)

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                soccerMatchService.updateMatch(1L, updateDTO));

        assertEquals("400 BAD_REQUEST \"O novo team mandante não pode ser igual ao team visitante atual.\"", exception.getMessage());
        verify(soccerMatchRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(2L);
        verify(soccerMatchRepository, never()).save(any(SoccerMatch.class));
    }

    @Test
    @DisplayName("Deve remover uma partida com sucesso")
    void shouldRemoveMatchSuccessfully() {
        when(soccerMatchRepository.findById(1L)).thenReturn(Optional.of(soccermatch));
        doNothing().when(soccerMatchRepository).delete(soccermatch);

        soccerMatchService.removeMatch(1L);

        verify(soccerMatchRepository, times(1)).findById(1L);
        verify(soccerMatchRepository, times(1)).delete(soccermatch);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover partida não encontrada")
    void shouldThrowExceptionWhenRemoveMatchNotFound() {
        when(soccerMatchRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                soccerMatchService.removeMatch(99L));

        assertEquals("404 NOT_FOUND \"Partida não encontrada\"", exception.getMessage());
        verify(soccerMatchRepository, times(1)).findById(99L);
        verify(soccerMatchRepository, never()).delete(any(SoccerMatch.class));
    }

    @Test
    @DisplayName("Deve encontrar uma partida por ID com sucesso")
    void shouldFindMatchByIdSuccessfully() {
        when(soccerMatchRepository.findById(1L)).thenReturn(Optional.of(soccermatch));

        SoccerMatch foundSoccerMatch = soccerMatchService.findMatchById(1L);

        assertNotNull(foundSoccerMatch);
        assertEquals(soccermatch.getSoccerMatchId(), foundSoccerMatch.getSoccerMatchId());
        verify(soccerMatchRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar partida por ID")
    void shouldThrowExceptionWhenFindMatchByIdNotFound() {
        when(soccerMatchRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                soccerMatchService.findMatchById(99L));

        assertEquals("404 NOT_FOUND \"Partida não encontrada\"", exception.getMessage());
        verify(soccerMatchRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar partidas sem filtros")
    void shouldListAllMatchesWhenNoFilters() {
        Page<SoccerMatch> matchPage = new PageImpl<>(Collections.singletonList(soccermatch));
        when(soccerMatchRepository.findAll(pageable)).thenReturn(matchPage);

        Page<SoccerMatch> result = soccerMatchService.listMatch(null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(soccerMatchRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar partidas filtrando por ID do time")
    void shouldListMatchesFilteredByTeamId() {
        Page<SoccerMatch> matchPage = new PageImpl<>(Collections.singletonList(soccermatch));
        when(soccerMatchRepository.findByHomeTeamIdOrAwayTeamId(homeTeam.getId(), homeTeam.getId(), pageable)).thenReturn(matchPage);

        Page<SoccerMatch> result = soccerMatchService.listMatch(homeTeam.getId(), null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(soccerMatchRepository, times(1)).findByHomeTeamIdOrAwayTeamId(homeTeam.getId(), homeTeam.getId(), pageable);
    }

    @Test
    @DisplayName("Deve listar partidas filtrando por ID do estádio")
    void shouldListMatchesFilteredByStadiumId() {
        Page<SoccerMatch> matchPage = new PageImpl<>(Collections.singletonList(soccermatch));
        when(soccerMatchRepository.findByStadiumId(stadium.getStadiumId(), pageable)).thenReturn(matchPage);

        Page<SoccerMatch> result = soccerMatchService.listMatch(null, stadium.getStadiumId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(soccerMatchRepository, times(1)).findByStadiumId(stadium.getStadiumId(), pageable);
    }

    @Test
    @DisplayName("Deve listar partidas filtrando por ID do time e ID do estádio")
    void shouldListMatchesFilteredByTeamAndStadiumId() {
        Page<SoccerMatch> matchPage = new PageImpl<>(Collections.singletonList(soccermatch));
        when(soccerMatchRepository.findByHomeTeamIdOrAwayTeamIdAndStadiumId(homeTeam.getId(), stadium.getStadiumId(), pageable)).thenReturn(matchPage);

        Page<SoccerMatch> result = soccerMatchService.listMatch(homeTeam.getId(), stadium.getStadiumId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(soccerMatchRepository, times(1)).findByHomeTeamIdOrAwayTeamIdAndStadiumId(homeTeam.getId(), stadium.getStadiumId(), pageable);
    }

    @Test
    @DisplayName("Deve listar goleadas para um time com sucesso")
    void shouldListLandslidesForTeamSuccessfully() {
        Page<SoccerMatch> landslidePage = new PageImpl<>(Collections.singletonList(soccermatch)); // Assume match is a landslide
        when(soccerMatchRepository.findLandslideByTeam(homeTeam.getId(), pageable)).thenReturn(landslidePage);

        Page<SoccerMatch> result = soccerMatchService.listLandslides(homeTeam.getId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(soccerMatchRepository, times(1)).findLandslideByTeam(homeTeam.getId(), pageable);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar goleadas sem ID do time")
    void shouldThrowExceptionWhenListLandslidesWithoutTeamId() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                soccerMatchService.listLandslides(null, pageable));

        assertEquals("400 BAD_REQUEST \"ID do team é obrigatório para filtrar goleadas\"", exception.getMessage());
        verify(soccerMatchRepository, never()).findLandslideByTeam(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar confrontos entre dois times com sucesso")
    void shouldListClashesSuccessfully() {
        Page<SoccerMatch> clashPage = new PageImpl<>(Collections.singletonList(soccermatch));
        when(soccerMatchRepository.findClashes(homeTeam.getId(), awayTeam.getId(), pageable)).thenReturn(clashPage);

        Page<SoccerMatch> result = soccerMatchService.listClashes(homeTeam.getId(), awayTeam.getId(), false, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(soccerMatchRepository, times(1)).findClashes(homeTeam.getId(), awayTeam.getId(), pageable);
        verify(soccerMatchRepository, never()).findLandslideClashes(anyLong(), anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar goleadas entre dois times com sucesso")
    void shouldListLandslideClashesSuccessfully() {
        Page<SoccerMatch> landslideClashPage = new PageImpl<>(Collections.singletonList(soccermatch));
        when(soccerMatchRepository.findLandslideClashes(homeTeam.getId(), awayTeam.getId(), pageable)).thenReturn(landslideClashPage);

        Page<SoccerMatch> result = soccerMatchService.listClashes(homeTeam.getId(), awayTeam.getId(), true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(soccerMatchRepository, times(1)).findLandslideClashes(homeTeam.getId(), awayTeam.getId(), pageable);
        verify(soccerMatchRepository, never()).findClashes(anyLong(), anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar confrontos com times iguais")
    void shouldThrowExceptionWhenListClashesWithSameTeams() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                soccerMatchService.listClashes(homeTeam.getId(), homeTeam.getId(), false, pageable));

        assertEquals("400 BAD_REQUEST \"Os IDs dos clubes não podem ser os mesmos para um confronto.\"", exception.getMessage());
        verify(soccerMatchRepository, never()).findClashes(anyLong(), anyLong(), any(Pageable.class));
        verify(soccerMatchRepository, never()).findLandslideClashes(anyLong(), anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar retrospecto de confronto entre dois times com sucesso")
    void shouldReturnClashesRetrospectSuccessfully() {
        Object[] stats = {5, 2, 1, 2, 8, 7, 1}; // totalJogos, vitoriasTeam1, empates, vitoriasTeam2, goalsTeam1, goalsTeam2, goalsBalance
        List<Object[]> results = Collections.singletonList(stats);

        when(teamRepository.findById(homeTeam.getId())).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(awayTeam.getId())).thenReturn(Optional.of(awayTeam));
        when(soccerMatchRepository.calculateClashesRetrospect(homeTeam.getId(), awayTeam.getId())).thenReturn(results);

        RetrospectVersusDTO retrospect = soccerMatchService.getRetrospectPlays(homeTeam.getId(), awayTeam.getId());

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

        verify(teamRepository, times(1)).findById(homeTeam.getId());
        verify(teamRepository, times(1)).findById(awayTeam.getId());
        verify(soccerMatchRepository, times(1)).calculateClashesRetrospect(homeTeam.getId(), awayTeam.getId());
    }

    @Test
    @DisplayName("Deve retornar retrospecto de confronto com zero quando não houver partidas")
    void shouldReturnZeroClashesRetrospectWhenNoMatches() {
        when(teamRepository.findById(homeTeam.getId())).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(awayTeam.getId())).thenReturn(Optional.of(awayTeam));
        when(soccerMatchRepository.calculateClashesRetrospect(homeTeam.getId(), awayTeam.getId())).thenReturn(Collections.emptyList());

        RetrospectVersusDTO retrospect = soccerMatchService.getRetrospectPlays(homeTeam.getId(), awayTeam.getId());

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

        verify(teamRepository, times(1)).findById(homeTeam.getId());
        verify(teamRepository, times(1)).findById(awayTeam.getId());
        verify(soccerMatchRepository, times(1)).calculateClashesRetrospect(homeTeam.getId(), awayTeam.getId());
    }


    @Test
    @DisplayName("Deve lançar exceção ao obter retrospecto de confronto com times iguais")
    void shouldThrowExceptionWhenGetClashesRetrospectWithSameTeams() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                soccerMatchService.getRetrospectPlays(homeTeam.getId(), homeTeam.getId()));

        assertEquals("400 BAD_REQUEST \"Os IDs dos clubes não podem ser os mesmos para obter o retrospecto de confronto.\"", exception.getMessage());
        verify(teamRepository, never()).findById(anyLong());
        verify(soccerMatchRepository, never()).calculateClashesRetrospect(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Deve retornar ranking por número de jogos")
    void shouldReturnRankingByPlays() {
        RankingDTO rankingDTO = new RankingDTO(homeTeam, 10L);
        when(soccerMatchRepository.rankingByPlays()).thenReturn(Collections.singletonList(rankingDTO));

        List<RankingDTO> ranking = soccerMatchService.getRanking("jogos");

        assertNotNull(ranking);
        assertFalse(ranking.isEmpty());
        assertEquals(1, ranking.size());
        assertEquals(rankingDTO, ranking.get(0));
        verify(soccerMatchRepository, times(1)).rankingByPlays();
    }

    @Test
    @DisplayName("Deve retornar ranking por vitórias")
    void shouldReturnRankingByVictories() {
        RankingDTO rankingDTO = new RankingDTO(homeTeam, 5L);
        when(soccerMatchRepository.rankingByVictories()).thenReturn(Collections.singletonList(rankingDTO));

        List<RankingDTO> ranking = soccerMatchService.getRanking("victories");

        assertNotNull(ranking);
        assertFalse(ranking.isEmpty());
        assertEquals(1, ranking.size());
        assertEquals(rankingDTO, ranking.get(0));
        verify(soccerMatchRepository, times(1)).rankingByVictories();
    }

    @Test
    @DisplayName("Deve retornar ranking por gols")
    void shouldReturnRankingByGoals() {
        RankingDTO rankingDTO = new RankingDTO(homeTeam, 20L);
        when(soccerMatchRepository.rankingByGoals()).thenReturn(Collections.singletonList(rankingDTO));

        List<RankingDTO> ranking = soccerMatchService.getRanking("gols");

        assertNotNull(ranking);
        assertFalse(ranking.isEmpty());
        assertEquals(1, ranking.size());
        assertEquals(rankingDTO, ranking.get(0));
        verify(soccerMatchRepository, times(1)).rankingByGoals();
    }

    @Test
    @DisplayName("Deve retornar ranking por pontos")
    void shouldReturnRankingByPoints() {
        RankingDTO rankingDTO = new RankingDTO(homeTeam, 16L); // 5 victories * 3 + 1 tie * 1 = 16
        when(soccerMatchRepository.rankingByPoints()).thenReturn(Collections.singletonList(rankingDTO));

        List<RankingDTO> ranking = soccerMatchService.getRanking("pontos");

        assertNotNull(ranking);
        assertFalse(ranking.isEmpty());
        assertEquals(1, ranking.size());
        assertEquals(rankingDTO, ranking.get(0));
        verify(soccerMatchRepository, times(1)).rankingByPoints();
    }

    @Test
    @DisplayName("Deve lançar exceção para critério de ranking inválido")
    void shouldThrowExceptionForInvalidRankingCriteria() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                soccerMatchService.getRanking("invalido"));

        assertEquals("400 BAD_REQUEST \"Critério de ranking inválido\"", exception.getMessage());
        verify(soccerMatchRepository, never()).rankingByPlays();
        verify(soccerMatchRepository, never()).rankingByVictories();
        verify(soccerMatchRepository, never()).rankingByGoals();
        verify(soccerMatchRepository, never()).rankingByPoints();
    }
}