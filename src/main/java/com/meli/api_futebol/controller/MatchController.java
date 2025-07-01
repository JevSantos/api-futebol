package com.meli.api_futebol.controller;

import com.meli.api_futebol.dto.MatchDTO;
import com.meli.api_futebol.dto.RankingDTO;
import com.meli.api_futebol.dto.RetrospectVersusDTO;
import com.meli.api_futebol.model.Match;
import com.meli.api_futebol.service.MatchService;
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
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<Match> create(@RequestBody @Valid MatchDTO dto) {
        Match match = matchService.createMatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> update(@PathVariable Long id, @RequestBody @Valid MatchDTO dto) {
        Match match = matchService.updateMatch(id, dto);
        return ResponseEntity.ok(match);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        matchService.removeMatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> findById(@PathVariable Long id) {
        Match match = matchService.findMatchById(id);
        return ResponseEntity.ok(match);
    }

    @GetMapping
    public ResponseEntity<Page<Match>> list(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Long stadiumId,
            @PageableDefault(sort = "matchDateTime", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Match> matches = matchService.listMatch(teamId, stadiumId, pageable);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/goleadas")
    public ResponseEntity<Page<Match>> listLandslides(
            @RequestParam Long teamId,
            @PageableDefault Pageable pageable) {

        Page<Match> partidas = matchService.listarGoleadas(teamId, pageable);
        return ResponseEntity.ok(partidas);
    }

    @GetMapping("/confrontos")
    public ResponseEntity<Page<Match>> listPlays(
            @RequestParam Long team1Id,
            @RequestParam Long team2Id,
            @RequestParam(required = false) Boolean goleadas,
            @PageableDefault(sort = "matchDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Match> matches = matchService.listClashes(team1Id, team2Id, goleadas, pageable);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/confrontos/retrospecto")
    public ResponseEntity<RetrospectVersusDTO> getRetrospectPlays(
            @RequestParam Long team1Id,
            @RequestParam Long team2Id) {
        RetrospectVersusDTO retrospectPlays = matchService.getRetrospectPlays(team1Id, team2Id);
        return ResponseEntity.ok(retrospectPlays);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<RankingDTO>> getRanking(
            @RequestParam(defaultValue = "pontos") String criteria) {

        List<RankingDTO> ranking = matchService.getRanking(criteria);
        return ResponseEntity.ok(ranking);
    }
}