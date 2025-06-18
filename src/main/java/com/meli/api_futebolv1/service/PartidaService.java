package com.meli.api_futebolv1.service;


import com.meli.api_futebolv1.dto.PartidaDTO;
import com.meli.api_futebolv1.model.Partida;
import com.meli.api_futebolv1.repository.ClubeRepository;
import com.meli.api_futebolv1.repository.PartidaRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PartidaService {
    private final PartidaRepository partidaRepository;
    private final ClubeRepository clubeRepository;
    public Partida criarPartida(PartidaDTO dto) {
        Partida partida = new Partida();

        partida.setClubeMandante(clubeRepository.findById(dto.clubeMandanteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube mandante não encontrado")));

        partida.setClubeVisitante(clubeRepository.findById(dto.clubeVisitanteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube visitante não encontrado")));

        partida.setPlacar(dto.placar());
        partida.setEstadio(dto.estadio());
        partida.setDataHora(dto.dataHora());

        return partidaRepository.save(partida);
    }

    public Partida atualizarPartida(Long id, PartidaDTO dto) {
        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada"));

        partida.setClubeMandante(clubeRepository.findById(dto.clubeMandanteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube mandante não encontrado")));

        partida.setClubeVisitante(clubeRepository.findById(dto.clubeVisitanteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube visitante não encontrado")));

        partida.setPlacar(dto.placar());
        partida.setEstadio(dto.estadio());
        partida.setDataHora(dto.dataHora());

        return partidaRepository.save(partida);
    }

    public void deletarPartida(Long id) {
        if (!partidaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada");
        }
        partidaRepository.deleteById(id);
    }

    public Partida buscarPorId(Long id) {
        return partidaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada"));
    }

    public Page<Partida> listarTodos(Long clubeId, String estadio, Pageable pageable) {
        if (clubeId != null) {
            return partidaRepository.findByClubeMandanteIdOrClubeVisitanteId(clubeId, clubeId, pageable);
        } else if (estadio != null) {
            return partidaRepository.findByEstadioContainingIgnoreCase(estadio, pageable);
        }
        return partidaRepository.findAll(pageable);
    }
}
