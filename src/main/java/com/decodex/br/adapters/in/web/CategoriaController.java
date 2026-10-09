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
import com.decodex.br.domain.port.in.CategoriaInputPort;
import com.decodex.br.adapters.in.web.documentation.CategoriaControllerDoc;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
public class CategoriaController implements CategoriaControllerDoc {

    private final CategoriaInputPort inputPort;

    public CategoriaController(CategoriaInputPort inputPort) {
        this.inputPort = inputPort;
    }

    @GetMapping(value = {"", "/paginada"})
    public ResponseEntity<PageResult<CategoriaResponseDTO>> findAll(
            CategoriaFilter filter,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "pagina", required = false) Integer pagina,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "tamanho", required = false) Integer tamanho,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "ordenar_por", required = false) String ordenarPor,
            @RequestParam(name = "direction", required = false) String direction,
            @RequestParam(name = "direcao", required = false) String direcao) {

        int resolvedPage = page != null ? page : (pagina != null ? pagina : 0);
        int resolvedSize = size != null ? size : (tamanho != null ? tamanho : 10);
        String resolvedSort = sort != null ? sort : ordenarPor;
        String resolvedDir = direction != null ? direction : direcao;

        PageRequest pageRequest = PageRequest.of(resolvedPage, resolvedSize, resolvedSort, resolvedDir);
        return ResponseEntity.ok(inputPort.findAll(filter, pageRequest)
                .map(CategoriaResponseDTO::from));
    }

    @Override
    public ResponseEntity<PageResult<CategoriaResponseDTO>> findAll(
            CategoriaFilter filter,
            int page,
            int size,
            String sort,
            String direction) {
        return findAll(filter, page, null, size, null, sort, null, direction, null);
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
    public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable java.util.UUID id) {
        Categoria categoria = inputPort.findById(id);
        return ResponseEntity.ok(CategoriaResponseDTO.from(categoria));
    }

    @Override
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> create(@RequestBody @Valid CategoriaCreateDTO dto) {
        Categoria categoria = inputPort.create(dto.toDomain());

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
            @PathVariable java.util.UUID id,
            @RequestBody @Valid CategoriaUpdateDTO dto) {

        Categoria atualizada = inputPort.update(id, dto.toDomain());

        return ResponseEntity.ok(CategoriaResponseDTO.from(atualizada));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable java.util.UUID id) {
        inputPort.delete(id);
        return ResponseEntity.noContent().build();
    }
}