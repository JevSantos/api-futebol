package com.meli.api_futebol.service;

import com.meli.api_futebol.dto.EstadioDTO;
import com.meli.api_futebol.model.Stadium;
import com.meli.api_futebol.repository.EstadioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StadiumService {

    private final EstadioRepository estadioRepository;

    public Stadium cadastrar(EstadioDTO dto) {
        Stadium stadium = new Stadium();
        stadium.setStadiumName(dto.nome());
        stadium.setLocationOfStadium(dto.cidade());
        return estadioRepository.save(stadium);
    }

    public Stadium atualizar(Long id, EstadioDTO dto) {
        Stadium stadium = buscarPorId(id);
        stadium.setStadiumName(dto.nome());
        stadium.setLocationOfStadium(dto.cidade());
        return estadioRepository.save(stadium);
    }

    public Stadium buscarPorId(Long id) {
        return estadioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estádio não encontrado"));
    }

    public Page<Stadium> listar(Pageable pageable) {
        return estadioRepository.findAll(pageable);
    }
}