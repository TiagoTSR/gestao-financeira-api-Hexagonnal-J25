package com.decodex.br.adapters.in.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.decodex.br.application.dto.categoria.CategoriaCreateDTO;
import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.categoria.CategoriaUpdateDTO;
import com.decodex.br.application.mapper.CategoriaDTOMapper;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.port.in.CategoriaUseCase;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaUseCase categoriaUseCase;

    public CategoriaController(CategoriaUseCase categoriaUseCase) {
        this.categoriaUseCase = categoriaUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> findAll() {
        List<CategoriaResponseDTO> response = categoriaUseCase.findAll()
            .stream()
            .map(CategoriaDTOMapper::toDTO)
            .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable Long id) {
        Categoria categoria = categoriaUseCase.findById(id);
        return ResponseEntity.ok(CategoriaDTOMapper.toDTO(categoria));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> create(@RequestBody @Valid CategoriaCreateDTO dto) {
        Categoria categoria = categoriaUseCase.create(CategoriaDTOMapper.toDomain(dto));

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(categoria.getId())
            .toUri();

        return ResponseEntity.created(location).body(CategoriaDTOMapper.toDTO(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid CategoriaUpdateDTO dto) {

        Categoria novosDados = CategoriaDTOMapper.toDomain(dto);
        Categoria atualizada = categoriaUseCase.update(id, novosDados);

        return ResponseEntity.ok(CategoriaDTOMapper.toDTO(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}