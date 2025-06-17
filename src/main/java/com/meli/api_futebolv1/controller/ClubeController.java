package com.meli.api_futebolv1.controller;


import com.meli.api_futebolv1.dto.ClubeDTO;
import com.meli.api_futebolv1.model.Clube;
import com.meli.api_futebolv1.service.ClubeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clubes")
@RequiredArgsConstructor
public class ClubeController {
    private final ClubeService service;

    @PostMapping
    public ResponseEntity<Clube> criarClube(@RequestBody @Valid ClubeDTO dto) {
        Clube clube = service.criarClube(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clube);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Clube> atualizarClube(@PathVariable Long id, @RequestBody @Valid ClubeDTO dto) {
        Clube clube = service.atualizarClube(id, dto);
        return ResponseEntity.ok(clube);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativarClube(@PathVariable Long id) {
        service.inativarClube(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clube> buscarPorId(@PathVariable Long id) {
        Clube clube = service.buscarPorId(id);
        return ResponseEntity.ok(clube);
    }

    @GetMapping
    public ResponseEntity<Page<Clube>> listarTodos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(sort = "nome") Pageable pageable) {

        Page<Clube> clubes = service.listarTodos(nome, estado, ativo, pageable);
        return ResponseEntity.ok(clubes);
    }
}
