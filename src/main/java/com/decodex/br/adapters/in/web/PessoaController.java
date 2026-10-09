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

import com.decodex.br.application.dto.pessoa.PessoaCreateDTO;
import com.decodex.br.application.dto.pessoa.PessoaResponseDTO;
import com.decodex.br.application.dto.pessoa.PessoaUpdateDTO;
import com.decodex.br.application.dto.pessoa.PessoaFilter;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.PessoaInputPort;
import com.decodex.br.adapters.in.web.documentation.PessoaControllerDoc;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pessoas")
public class PessoaController implements PessoaControllerDoc {

    private final PessoaInputPort inputPort;

    public PessoaController(PessoaInputPort inputPort) {
        this.inputPort = inputPort;
    }

    @GetMapping(value = {"", "/paginada"})
    public ResponseEntity<PageResult<PessoaResponseDTO>> findAll(
            PessoaFilter filter,
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
                .map(PessoaResponseDTO::from));
    }

    @Override
    public ResponseEntity<PageResult<PessoaResponseDTO>> findAll(
            PessoaFilter filter,
            int page,
            int size,
            String sort,
            String direction) {
        return findAll(filter, page, null, size, null, sort, null, direction, null);
    }

    // Sobrecarga de conveniência para testes e chamadas programáticas
    public ResponseEntity<PageResult<PessoaResponseDTO>> findAll(
            PessoaFilter filter,
            int page,
            int size) {
        return findAll(filter, page, size, null, null);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> findById(@PathVariable java.util.UUID id) {
        Pessoa pessoa = inputPort.findById(id);
        return ResponseEntity.ok(PessoaResponseDTO.from(pessoa));
    }

    @Override
    @PostMapping
    public ResponseEntity<PessoaResponseDTO> create(@RequestBody @Valid PessoaCreateDTO dto) {
        Pessoa pessoa = inputPort.create(dto.toDomain());

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(pessoa.getId())
            .toUri();

        return ResponseEntity.created(location).body(PessoaResponseDTO.from(pessoa));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> update(
            @PathVariable java.util.UUID id,
            @RequestBody @Valid PessoaUpdateDTO dto) {

        Pessoa atualizada = inputPort.update(id, dto.toDomain());

        return ResponseEntity.ok(PessoaResponseDTO.from(atualizada));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable java.util.UUID id) {
        inputPort.delete(id);
        return ResponseEntity.noContent().build();
    }
}