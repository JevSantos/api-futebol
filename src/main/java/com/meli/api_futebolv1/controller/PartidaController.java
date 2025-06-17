package com.meli.api_futebolv1.controller;


import com.meli.api_futebolv1.dto.PartidaDTO;
import com.meli.api_futebolv1.model.Partida;
import com.meli.api_futebolv1.service.PartidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partidas")
@RequiredArgsConstructor
public class PartidaController {
    private final PartidaService service;

    @PostMapping
    public ResponseEntity<Partida> criarPartida(@RequestBody @Valid PartidaDTO dto) {
        Partida partida = service.criarPartida(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(partida);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Partida> atualizarPartida(@PathVariable Long id, @RequestBody @Valid PartidaDTO dto) {
        Partida partida = service.atualizarPartida(id, dto);
        return ResponseEntity.ok(partida);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarPartida(@PathVariable Long id) {
        service.deletarPartida(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partida> buscarPorId(@PathVariable Long id) {
        Partida partida = service.buscarPorId(id);
        return ResponseEntity.ok(partida);
    }

    @GetMapping
    public ResponseEntity<Page<Partida>> listarTodos(
            @RequestParam(required = false) Long clubeId,
            @RequestParam(required = false) String estadio,
            @PageableDefault(sort = "dataHora") Pageable pageable) {

        Page<Partida> partidas = service.listarTodos(clubeId, estadio, pageable);
        return ResponseEntity.ok(partidas);
    }
}
