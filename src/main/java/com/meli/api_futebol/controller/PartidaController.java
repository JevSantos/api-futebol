package com.meli.api_futebol.controller;

import com.meli.api_futebol.dto.PartidaDTO;
import com.meli.api_futebol.dto.RankingDTO;
import com.meli.api_futebol.dto.RetrospectoConfrontoDTO;
import com.meli.api_futebol.model.Match;
import com.meli.api_futebol.service.PartidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidas")
@RequiredArgsConstructor
public class PartidaController {

    private final PartidaService partidaService;

    @PostMapping
    public ResponseEntity<Match> cadastrar(@RequestBody @Valid PartidaDTO dto) {
        Match partida = partidaService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(partida);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> atualizar(@PathVariable Long id, @RequestBody @Valid PartidaDTO dto) {
        Match partida = partidaService.atualizar(id, dto);
        return ResponseEntity.ok(partida);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        partidaService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> buscarPorId(@PathVariable Long id) {
        Match partida = partidaService.buscarPorId(id);
        return ResponseEntity.ok(partida);
    }

    @GetMapping
    public ResponseEntity<Page<Match>> listar(
            @RequestParam(required = false) Long clubeId,
            @RequestParam(required = false) Long estadioId,
            @PageableDefault(sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Match> partidas = partidaService.listar(clubeId, estadioId, pageable);
        return ResponseEntity.ok(partidas);
    }

    @GetMapping("/goleadas")
    public ResponseEntity<Page<Match>> listarGoleadas(
            @RequestParam Long clubeId,
            @PageableDefault Pageable pageable) {

        Page<Match> partidas = partidaService.listarGoleadas(clubeId, pageable);
        return ResponseEntity.ok(partidas);
    }

    @GetMapping("/confrontos")
    public ResponseEntity<Page<Match>> listarConfrontos(
            @RequestParam Long clube1Id,
            @RequestParam Long clube2Id,
            @RequestParam(required = false) Boolean goleadas,
            @PageableDefault(sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Match> partidas = partidaService.listarConfrontos(clube1Id, clube2Id, goleadas, pageable);
        return ResponseEntity.ok(partidas);
    }

    @GetMapping("/confrontos/retrospecto")
    public ResponseEntity<RetrospectoConfrontoDTO> getRetrospectoConfronto(
            @RequestParam Long clube1Id,
            @RequestParam Long clube2Id) {

        RetrospectoConfrontoDTO retrospecto = partidaService.getRetrospectoConfronto(clube1Id, clube2Id);
        return ResponseEntity.ok(retrospecto);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<RankingDTO>> getRanking(
            @RequestParam(defaultValue = "pontos") String criterio) {

        List<RankingDTO> ranking = partidaService.getRanking(criterio);
        return ResponseEntity.ok(ranking);
    }
}

