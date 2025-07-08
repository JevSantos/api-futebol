package com.meli.api_futebol.service;

import com.meli.api_futebol.dto.StadiumDTO;
import com.meli.api_futebol.model.Stadium;
import com.meli.api_futebol.repository.StadiumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StadiumServiceTest {

    @Mock
    private StadiumRepository stadiumRepository;

    @InjectMocks
    private StadiumService stadiumService;

    private Stadium stadium;
    private StadiumDTO stadiumDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        stadium = new Stadium();
        stadium.setStadiumId(1L);
        stadium.setStadiumName("Maracanã");
        stadium.setStadiumCity("Rio de Janeiro");
        stadium.setStadiumOwner("Estado do Rio de Janeiro");

        stadiumDTO = new StadiumDTO(
                "Maracanã",
                "Rio de Janeiro",
                "Estado do Rio de Janeiro"
        );

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar um novo estádio com sucesso")
    void shouldCreateStadiumSuccessfully() {
        when(stadiumRepository.save(any(Stadium.class))).thenReturn(stadium);

        Stadium createdStadium = stadiumService.createStadium(stadiumDTO);

        assertNotNull(createdStadium);
        assertEquals(stadium.getStadiumName(), createdStadium.getStadiumName());
        assertEquals(stadium.getStadiumCity(), createdStadium.getStadiumCity());
        assertEquals(stadium.getStadiumOwner(), createdStadium.getStadiumOwner());
        verify(stadiumRepository, times(1)).save(any(Stadium.class));
    }

    @Test
    @DisplayName("Deve atualizar um estádio existente com sucesso")
    void shouldUpdateStadiumSuccessfully() {
        Stadium updatedStadium = new Stadium();
        updatedStadium.setStadiumId(1L);
        updatedStadium.setStadiumName("Maracanã Reformado");
        updatedStadium.setStadiumCity("Niterói");
        updatedStadium.setStadiumOwner("Consórcio");

        StadiumDTO updatedStadiumDTO = new StadiumDTO(
                "Maracanã Reformado",
                "Niterói",
                "Consórcio"
        );

        when(stadiumRepository.findById(1L)).thenReturn(Optional.of(stadium));
        when(stadiumRepository.save(any(Stadium.class))).thenReturn(updatedStadium);

        Stadium result = stadiumService.updateStadium(1L, updatedStadiumDTO);

        assertNotNull(result);
        assertEquals(updatedStadium.getStadiumName(), result.getStadiumName());
        assertEquals(updatedStadium.getStadiumCity(), result.getStadiumCity());
        assertEquals(updatedStadium.getStadiumOwner(), result.getStadiumOwner());
        verify(stadiumRepository, times(1)).findById(1L);
        verify(stadiumRepository, times(1)).save(any(Stadium.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar estádio não encontrado")
    void shouldThrowExceptionWhenUpdateStadiumNotFound() {
        when(stadiumRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                stadiumService.updateStadium(99L, stadiumDTO));

        assertEquals("404 NOT_FOUND \"Estádio não encontrado\"", exception.getMessage());
        verify(stadiumRepository, times(1)).findById(99L);
        verify(stadiumRepository, never()).save(any(Stadium.class));
    }

    @Test
    @DisplayName("Deve encontrar um estádio por ID com sucesso")
    void shouldFindStadiumByIdSuccessfully() {
        when(stadiumRepository.findById(1L)).thenReturn(Optional.of(stadium));

        Stadium foundStadium = stadiumService.findStadiumById(1L);

        assertNotNull(foundStadium);
        assertEquals(stadium.getStadiumId(), foundStadium.getStadiumId());
        verify(stadiumRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar estádio por ID")
    void shouldThrowExceptionWhenFindStadiumByIdNotFound() {
        when(stadiumRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                stadiumService.findStadiumById(99L));

        assertEquals("404 NOT_FOUND \"Estádio não encontrado\"", exception.getMessage());
        verify(stadiumRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todos os estádios com sucesso")
    void shouldListAllStadiumsSuccessfully() {
        Page<Stadium> stadiumPage = new PageImpl<>(Collections.singletonList(stadium));
        when(stadiumRepository.findAll(pageable)).thenReturn(stadiumPage);

        Page<Stadium> result = stadiumService.listStadium(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(stadium.getStadiumName(), result.getContent().get(0).getStadiumName());
        verify(stadiumRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao listar estádios sem resultados")
    void shouldReturnEmptyListWhenListStadiumsNoResults() {
        Page<Stadium> emptyPage = new PageImpl<>(Collections.emptyList());
        when(stadiumRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Stadium> result = stadiumService.listStadium(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(stadiumRepository, times(1)).findAll(pageable);
    }
}
