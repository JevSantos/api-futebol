package com.meli.api_futebol.controller;

import com.meli.api_futebol.dto.EstadioDTO;
import com.meli.api_futebol.model.Stadium;
import com.meli.api_futebol.service.StadiumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadios")
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;

    @PostMapping
    public ResponseEntity<Stadium> entryClub(@RequestBody @Valid EstadioDTO dto) {
        Stadium stadium = stadiumService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(stadium);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stadium> atualizar(@PathVariable Long id, @RequestBody @Valid EstadioDTO dto) {
        Stadium stadium = stadiumService.atualizar(id, dto);
        return ResponseEntity.ok(stadium);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stadium> buscarPorId(@PathVariable Long id) {
        Stadium stadium = stadiumService.buscarPorId(id);
        return ResponseEntity.ok(stadium);
    }

    @GetMapping
    public ResponseEntity<Page<Stadium>> listar(@PageableDefault(sort = "nome") Pageable pageable) {
        Page<Stadium> estadios = stadiumService.listar(pageable);
        return ResponseEntity.ok(estadios);
    }
}
