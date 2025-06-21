package com.meli.api_futebol.controller;

import com.meli.api_futebol.dto.ClubeDTO;
import com.meli.api_futebol.dto.RetrospectoClubeDTO;
import com.meli.api_futebol.dto.RetrospectoConfrontoDTO;
import com.meli.api_futebol.model.Team;
import com.meli.api_futebol.service.ClubeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubes")
@RequiredArgsConstructor
public class ClubeController {

    private final ClubeService clubeService;

    @PostMapping
    public ResponseEntity<Team> cadastrar(@RequestBody @Valid ClubeDTO dto) {
        Team clube = clubeService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clube);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> atualizar(@PathVariable Long id, @RequestBody @Valid ClubeDTO dto) {
        Team clube = clubeService.atualizar(id, dto);
        return ResponseEntity.ok(clube);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        clubeService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> buscarPorId(@PathVariable Long id) {
        Team clube = clubeService.buscarPorId(id);
        return ResponseEntity.ok(clube);
    }

    @GetMapping
    public ResponseEntity<Page<Team>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(sort = "nome") Pageable pageable) {

        Page<Team> clubes = clubeService.listar(nome, estado, ativo, pageable);
        return ResponseEntity.ok(clubes);
    }

    @GetMapping("/{id}/retrospecto")
    public ResponseEntity<RetrospectoClubeDTO> getRetrospecto(@PathVariable Long id) {
        RetrospectoClubeDTO retrospecto = clubeService.getRetrospecto(id);
        return ResponseEntity.ok(retrospecto);
    }

    @GetMapping("/{id}/retrospecto-adversarios")
    public ResponseEntity<List<RetrospectoConfrontoDTO>> getRetrospectoContraAdversarios(@PathVariable Long id) {
        List<RetrospectoConfrontoDTO> retrospectos = clubeService.getRetrospectoContraAdversarios(id);
        return ResponseEntity.ok(retrospectos);
    }
}
