package com.meli.api_futebol.service;

import com.meli.api_futebol.dto.ClubeDTO;
import com.meli.api_futebol.dto.RetrospectoClubeDTO;
import com.meli.api_futebol.dto.RetrospectoConfrontoDTO;
import com.meli.api_futebol.model.Team;
import com.meli.api_futebol.repository.ClubeRepository;
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
public class ClubeService {

    private final ClubeRepository clubeRepository;
    private final PartidaRepository partidaRepository;

    public Team cadastrar(ClubeDTO dto) {
        Team clube = new Team();
        clube.setTeamName(dto.nome());
        clube.setTeamState(dto.estado());
        clube.setCreationDate(dto.dataCriacao() != null ? dto.dataCriacao() : LocalDate.now());
        clube.setActive(true);
        return clubeRepository.save(clube);
    }

    public Team atualizar(Long id, ClubeDTO dto) {
        Team clube = buscarPorId(id);
        clube.setTeamName(dto.nome());
        clube.setTeamState(dto.estado());
        clube.setCreationDate(dto.dataCriacao());
        return clubeRepository.save(clube);
    }

    public void inativar(Long id) {
        Team clube = buscarPorId(id);
        clube.setActive(false);
        clubeRepository.save(clube);
    }

    public Team buscarPorId(Long id) {
        return clubeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube não encontrado"));
    }

    public Page<Team> listar(String nome, String estado, Boolean ativo, Pageable pageable) {
        if (nome != null && estado != null && ativo != null) {
            return clubeRepository.findByNomeContainingIgnoreCaseAndEstadoAndAtivo(nome, estado, ativo, pageable);
        } else if (nome != null && estado != null) {
            return clubeRepository.findByNomeContainingIgnoreCaseAndEstado(nome, estado, pageable);
        } else if (nome != null && ativo != null) {
            return clubeRepository.findByNomeContainingIgnoreCaseAndAtivo(nome, ativo, pageable);
        } else if (estado != null && ativo != null) {
            return clubeRepository.findByEstadoAndAtivo(estado, ativo, pageable);
        } else if (nome != null) {
            return clubeRepository.findByNomeContainingIgnoreCase(nome, pageable);
        } else if (estado != null) {
            return clubeRepository.findByEstado(estado, pageable);
        } else if (ativo != null) {
            return clubeRepository.findByAtivo(ativo, pageable);
        }
        return clubeRepository.findAll(pageable);
    }


    public RetrospectoClubeDTO getRetrospecto(Long clubeId) {
        Team clube = buscarPorId(clubeId);

        List<Object[]> resultados = partidaRepository.calcularRetrospecto(clubeId);
        if (resultados.isEmpty()) {
            return new RetrospectoClubeDTO(clube, 0L, 0L, 0L, 0L, 0L, 0L);
        }

        Object[] estatisticas = resultados.get(0);
        return new RetrospectoClubeDTO(
                clube,
                ((Number) estatisticas[0]).longValue(),    // Jogos
                ((Number) estatisticas[1]).longValue(),    // vitorias
                ((Number) estatisticas[2]).longValue(),    // empates
                ((Number) estatisticas[3]).longValue(),    // derrotas
                ((Number) estatisticas[4]).longValue(),    // golsFeitos
                ((Number) estatisticas[5]).longValue()     // golsSofridos
        );
    }

    public List<RetrospectoConfrontoDTO> getRetrospectoContraAdversarios(Long clubeId) {
        buscarPorId(clubeId); // Verifica se o clube existe
        return partidaRepository.calcularRetrospectoContraAdversarios(clubeId);
    }
}