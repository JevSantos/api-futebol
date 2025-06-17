package com.meli.api_futebolv1.service;


import com.meli.api_futebolv1.dto.ClubeDTO;
import com.meli.api_futebolv1.model.Clube;
import com.meli.api_futebolv1.repository.ClubeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClubeService {
    private final ClubeRepository repository;

    public Clube criarClube(ClubeDTO dto) {
        Clube clube = new Clube();
        clube.setNome(dto.nome());
        clube.setEstado(dto.estado());
        clube.setDataCriacao(dto.dataCriacao());
        clube.setAtivo(dto.ativo());
        return repository.save(clube);
    }

    public Clube atualizarClube(Long id, ClubeDTO dto) {
        Clube clube = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube não encontrado"));

        clube.setNome(dto.nome());
        clube.setEstado(dto.estado());
        clube.setDataCriacao(dto.dataCriacao());
        clube.setAtivo(dto.ativo());

        return repository.save(clube);
    }

    @Transactional
    public void inativarClube(Long id) {
        Clube clube = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube não encontrado"));
        clube.setAtivo(false);
        repository.save(clube);
    }

    public Clube buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clube não encontrado"));
    }

    public Page<Clube> listarTodos(String nome, String estado, Boolean ativo, Pageable pageable) {
        if (nome != null) {
            return repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else if (estado != null) {
            return repository.findByEstado(estado, pageable);
        } else if (ativo != null) {
            return repository.findByAtivo(ativo, pageable);
        }
        return repository.findAll(pageable);
    }
}