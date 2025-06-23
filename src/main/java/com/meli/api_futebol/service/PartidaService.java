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
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final TeamRepository teamRepository;
    private final EstadioRepository estadioRepository;

    public Match cadastrar(PartidaDTO dto) {
        Match partida = new Match();

        Team mandante = teamRepository.findById(dto.clubeMandanteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube mandante não encontrado"));

        Team visitante = teamRepository.findById(dto.clubeVisitanteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube visitante não encontrado"));

        Stadium stadium = estadioRepository.findById(dto.estadioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estádio não encontrado"));

        partida.setHomeTeam(mandante);
        partida.setAwayTeam(visitante);
        partida.setGoalsHomeTeam(dto.golsMandante());
        partida.setGoalsAwayTeam(dto.golsVisitante());
        partida.setStadium(stadium);
        partida.setDateAndTime(dto.dataHora() != null ? dto.dataHora() : LocalDateTime.now());

        return partidaRepository.save(partida);
    }

    public Match atualizar(Long id, PartidaDTO dto) {
        Match partida = buscarPorId(id);

        if (dto.clubeMandanteId() != null) {
            Team mandante = teamRepository.findById(dto.clubeMandanteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube mandante não encontrado"));
            partida.setHomeTeam(mandante);
        }

        if (dto.clubeVisitanteId() != null) {
            Team visitante = teamRepository.findById(dto.clubeVisitanteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube visitante não encontrado"));
            partida.setAwayTeam(visitante);
        }

        if (dto.estadioId() != null) {
            Stadium stadium = estadioRepository.findById(dto.estadioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estádio não encontrado"));
            partida.setStadium(stadium);
        }

        if (dto.golsMandante() != null) partida.setGoalsHomeTeam(dto.golsMandante());
        if (dto.golsVisitante() != null) partida.setGoalsAwayTeam(dto.golsVisitante());
        if (dto.dataHora() != null) partida.setDateAndTime(dto.dataHora());

        return partidaRepository.save(partida);
    }

    public void remover(Long id) {
        Match partida = buscarPorId(id);
        partidaRepository.delete(partida);
    }

    public Match buscarPorId(Long id) {
        return partidaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada"));
    }

    public Page<Match> listar(Long clubeId, Long estadioId, Pageable pageable) {
        if (clubeId != null && estadioId != null) {
            return partidaRepository.findByClubeMandanteIdOrClubeVisitanteIdAndEstadioId(clubeId, clubeId, estadioId, pageable);
        } else if (clubeId != null) {
            return partidaRepository.findByClubeMandanteIdOrClubeVisitanteId(clubeId, clubeId, pageable);
        } else if (estadioId != null) {
            return partidaRepository.findByEstadioId(estadioId, pageable);
        }
        return partidaRepository.findAll(pageable);
    }

    public Page<Match> listarGoleadas(Long clubeId, Pageable pageable) {
        if (clubeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do clube é obrigatório para filtrar goleadas");
        }
        return partidaRepository.findGoleadasByClube(clubeId, pageable);
    }

    public Page<Match> listarConfrontos(Long clube1Id, Long clube2Id, Boolean goleadas, Pageable pageable) {
        if (goleadas != null && goleadas) {
            return partidaRepository.findGoleadasConfrontos(clube1Id, clube2Id, pageable);
        }
        return partidaRepository.findConfrontos(clube1Id, clube2Id, pageable);
    }

    public RetrospectVersusDTO getRetrospectoConfronto(Long clube1Id, Long clube2Id) {
        Team clube1 = teamRepository.findById(clube1Id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube 1 não encontrado"));

        Team clube2 = teamRepository.findById(clube2Id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube 2 não encontrado"));

        List<Object[]> resultados = partidaRepository.calcularRetrospectoConfronto(clube1Id, clube2Id);

        if (resultados.isEmpty()) {
            return new RetrospectVersusDTO(clube1, clube2, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }

        Object[] stats = resultados.get(0);
        return new RetrospectVersusDTO(
                clube1,
                clube2,
                ((Number) stats[0]).longValue(),   // totalJogos
                ((Number) stats[1]).longValue(),    // vitoriasClube1
                ((Number) stats[2]).longValue(),    // empates
                ((Number) stats[3]).longValue(),    // vitoriasClube2
                ((Number) stats[4]).longValue(),    // golsClube1
                ((Number) stats[5]).longValue(),    // golsClube2
                ((Number) stats[6]).longValue()     // saldoClube1
        );
    }

    public List<RankingDTO> getRanking(String criterio) {
        switch (criterio) {
            case "jogos":
                return partidaRepository.rankearPorJogos();
            case "vitorias":
                return partidaRepository.rankearPorVitorias();
            case "gols":
                return partidaRepository.rankearPorGols();
            case "pontos":
                return partidaRepository.rankearPorPontos();
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Critério de ranking inválido");
        }
    }
}