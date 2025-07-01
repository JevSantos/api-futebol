package com.meli.api_futebol.service;

import com.meli.api_futebol.dto.StadiumDTO;
import com.meli.api_futebol.model.Stadium;
import com.meli.api_futebol.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StadiumService {
    private final StadiumRepository stadiumRepository;

    public Stadium createStadium(StadiumDTO dto) {
        Stadium stadium = new Stadium();
        stadium.setStadiumName(dto.stadiumName());
        stadium.setStadiumCity(dto.stadiumCity());
        stadium.setStadiumOwner(dto.stadiumOwner());
        return stadiumRepository.save(stadium);
    }
    public Stadium updateStadium(Long id, StadiumDTO dto) {
        Stadium stadium = findStadiumById(id);
        stadium.setStadiumName(dto.stadiumName());
        stadium.setStadiumCity(dto.stadiumCity());
        stadium.setStadiumOwner(dto.stadiumOwner());
        return stadiumRepository.save(stadium);
    }
    public Stadium findStadiumById(Long id) {
        return stadiumRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estádio não encontrado"));
    }

    public Page<Stadium> listStadium(@PageableDefault(sort = "stadiumName") Pageable pageable) {
        return stadiumRepository.findAll(pageable);
    }
}