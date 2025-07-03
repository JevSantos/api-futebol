package com.meli.api_futebol.controller;

import com.meli.api_futebol.dto.TeamDTO;
import com.meli.api_futebol.dto.TeamRetrospectDTO;
import com.meli.api_futebol.dto.RetrospectVersusDTO;
import com.meli.api_futebol.model.Team;
import com.meli.api_futebol.service.TeamService;
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
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<Team> create(@RequestBody @Valid TeamDTO dto) {
        Team team = teamService.registryTeam(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(team);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> update(@PathVariable Long id, @RequestBody @Valid TeamDTO dto) {
        Team team = teamService.teamUpdate(id, dto);
        return ResponseEntity.ok(team);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        teamService.teamDeactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> findTeamById(@PathVariable Long id) {
        Team clube = teamService.findTeamById(id);
        return ResponseEntity.ok(clube);
    }

    @GetMapping("/teams-list")
    public ResponseEntity<Page<Team>> listTeam(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(sort = "teamName") Pageable pageable) {

        Page<Team> clubes = teamService.listTeam(name, state, active, pageable);
        return ResponseEntity.ok(clubes);
    }

    @GetMapping("/{id}/retrospect")
    public ResponseEntity<TeamRetrospectDTO> getRetrospect(@PathVariable Long id) {
        TeamRetrospectDTO retrospect = teamService.getRetrospect(id);
        return ResponseEntity.ok(retrospect);
    }

    @GetMapping("/{id}/retrospecto-adversarios")
    public ResponseEntity<List<RetrospectVersusDTO>> getRetrospectAgainstRivals(@PathVariable Long id) {
        List<RetrospectVersusDTO> retrospectVersus = teamService.getRetrospectVersusRivals(id);
        return ResponseEntity.ok(retrospectVersus);
    }
}
