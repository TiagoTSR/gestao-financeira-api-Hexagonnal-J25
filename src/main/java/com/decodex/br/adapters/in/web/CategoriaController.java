package com.decodex.br.adapters.in.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.decodex.br.application.dto.categoria.CategoriaCreateDTO;
import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.categoria.CategoriaUpdateDTO;
import com.decodex.br.application.dto.categoria.CategoriaFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.CategoriaUseCase;
import com.decodex.br.adapters.in.web.documentation.CategoriaControllerDoc;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
public class CategoriaController implements CategoriaControllerDoc {

    private final CategoriaUseCase useCase;

    public CategoriaController(CategoriaUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    @GetMapping(value = {"", "/paginada"})
    public ResponseEntity<PageResult<CategoriaResponseDTO>> findAll(
            CategoriaFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {

        PageRequest pageRequest = PageRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(useCase.findAll(filter, pageRequest)
                .map(CategoriaResponseDTO::from));
    }

    // Sobrecarga de conveniência para uso programático e testes unitários
    public ResponseEntity<PageResult<CategoriaResponseDTO>> findAll(
            int page,
            int size,
            CategoriaFilter filter) {
        return findAll(filter, page, size, null, null);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable Long id) {
        Categoria categoria = useCase.findById(id);
        return ResponseEntity.ok(CategoriaResponseDTO.from(categoria));
    }

    @Override
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> create(@RequestBody @Valid CategoriaCreateDTO dto) {
        Categoria categoria = useCase.create(dto.toDomain());

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(categoria.getId())
            .toUri();

        return ResponseEntity.created(location).body(CategoriaResponseDTO.from(categoria));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid CategoriaUpdateDTO dto) {

        Categoria atualizada = useCase.update(id, dto.toDomain());

        return ResponseEntity.ok(CategoriaResponseDTO.from(atualizada));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}