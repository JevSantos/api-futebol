package com.meli.api_futebol.controller;

import com.meli.api_futebol.dto.StadiumDTO;
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
    public ResponseEntity<Stadium> createStadium(@RequestBody @Valid StadiumDTO dto) {
        Stadium stadium = stadiumService.createStadium(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(stadium);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stadium> updateStadium(@PathVariable Long id, @RequestBody @Valid StadiumDTO dto) {
        Stadium stadium = stadiumService.updateStadium(id, dto);
        return ResponseEntity.ok(stadium);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stadium> findStadiumById(@PathVariable Long id) {
        Stadium stadium = stadiumService.findStadiumById(id);
        return ResponseEntity.ok(stadium);
    }

    @GetMapping
    public ResponseEntity<Page<Stadium>> listStadium(@PageableDefault(sort = "teamName") Pageable pageable) {
        Page<Stadium> estadios = stadiumService.listStadium(pageable);
        return ResponseEntity.ok(estadios);
    }
}
