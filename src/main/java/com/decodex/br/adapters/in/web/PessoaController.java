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
import com.decodex.br.domain.filter.PessoaFilter;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.PessoaUseCase;
import com.decodex.br.adapters.in.web.documentation.PessoaControllerDoc;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pessoas")
public class PessoaController implements PessoaControllerDoc {

    private final PessoaUseCase useCase;

    public PessoaController(PessoaUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public PageResult<PessoaResponseDTO> findAll(
            PessoaFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0) {
            throw new IllegalArgumentException("O número da página não pode ser negativo.");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("O tamanho da página deve ser maior que zero.");
        }

        return useCase.findAll(filter, new PageRequest(page, size))
                .map(PessoaResponseDTO::from);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> findById(@PathVariable Long id) {
        Pessoa pessoa = useCase.findById(id);
        return ResponseEntity.ok(PessoaResponseDTO.from(pessoa));
    }

    @PostMapping
    public ResponseEntity<PessoaResponseDTO> create(@RequestBody @Valid PessoaCreateDTO dto) {
        Pessoa pessoa = useCase.create(dto.toDomain());

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(pessoa.getId())
            .toUri();

        return ResponseEntity.created(location).body(PessoaResponseDTO.from(pessoa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid PessoaUpdateDTO dto) {

        Pessoa atualizada = useCase.update(id, dto.toDomain());

        return ResponseEntity.ok(PessoaResponseDTO.from(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}