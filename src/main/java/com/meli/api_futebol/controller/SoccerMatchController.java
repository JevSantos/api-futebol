package com.meli.api_futebol.controller;

import com.meli.api_futebol.dto.SoccerMatchDTO;
import com.meli.api_futebol.dto.RankingDTO;
import com.meli.api_futebol.dto.RetrospectVersusDTO;
import com.meli.api_futebol.model.SoccerMatch;
import com.meli.api_futebol.service.SoccerMatchService;
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
public class SoccerMatchController {

    private final SoccerMatchService soccerMatchService;

    @PostMapping
    public ResponseEntity<SoccerMatch> create(@RequestBody @Valid SoccerMatchDTO dto) {
        SoccerMatch soccermatch = soccerMatchService.createMatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(soccermatch);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SoccerMatch> update(@PathVariable Long id, @RequestBody @Valid SoccerMatchDTO dto) {
        SoccerMatch soccermatch = soccerMatchService.updateMatch(id, dto);
        return ResponseEntity.ok(soccermatch);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        soccerMatchService.removeMatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoccerMatch> findById(@PathVariable Long id) {
        SoccerMatch soccermatch = soccerMatchService.findMatchById(id);
        return ResponseEntity.ok(soccermatch);
    }

    @GetMapping("/partidas")
    public ResponseEntity<Page<SoccerMatch>> list(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Long stadiumId,
            @PageableDefault(sort = "matchDateTime", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<SoccerMatch> matches = soccerMatchService.listMatch(teamId, stadiumId, pageable);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/goleadas")
    public ResponseEntity<Page<SoccerMatch>> listLandslides(
            @RequestParam Long teamId,
            @PageableDefault Pageable pageable) {

        Page<SoccerMatch> partidas = soccerMatchService.listLandslides(teamId, pageable);
        return ResponseEntity.ok(partidas);
    }

    @GetMapping("/confrontos")
    public ResponseEntity<Page<SoccerMatch>> listPlays(
            @RequestParam Long team1Id,
            @RequestParam Long team2Id,
            @RequestParam(required = false) Boolean goleadas,
            @PageableDefault(sort = "matchDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SoccerMatch> matches = soccerMatchService.listClashes(team1Id, team2Id, goleadas, pageable);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/confrontos/retrospecto")
    public ResponseEntity<RetrospectVersusDTO> getRetrospectPlays(
            @RequestParam Long team1Id,
            @RequestParam Long team2Id) {
        RetrospectVersusDTO retrospectPlays = soccerMatchService.getRetrospectPlays(team1Id, team2Id);
        return ResponseEntity.ok(retrospectPlays);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<RankingDTO>> getRanking(
            @RequestParam(defaultValue = "pontos") String criteria) {

        List<RankingDTO> ranking = soccerMatchService.getRanking(criteria);
        return ResponseEntity.ok(ranking);
    }
}