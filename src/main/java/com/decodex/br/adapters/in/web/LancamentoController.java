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

import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoResponseDTO;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.application.mapper.LancamentoDTOMapper;
import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.CategoriaUseCase;
import com.decodex.br.domain.port.in.LancamentoUseCase;
import com.decodex.br.domain.port.in.PessoaUseCase;
import com.decodex.br.adapters.in.web.documentation.LancamentoControllerDoc;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController implements LancamentoControllerDoc {

    private final LancamentoUseCase lancamentoUseCase;
    private final CategoriaUseCase categoriaUseCase;
    private final PessoaUseCase pessoaUseCase;
    private final LancamentoDTOMapper mapper;

    public LancamentoController(
            LancamentoUseCase lancamentoUseCase,
            CategoriaUseCase categoriaUseCase,
            PessoaUseCase pessoaUseCase,
            LancamentoDTOMapper mapper) {
        this.lancamentoUseCase = lancamentoUseCase;
        this.categoriaUseCase = categoriaUseCase;
        this.pessoaUseCase = pessoaUseCase;
        this.mapper = mapper;
    }

    @Override
    @GetMapping(value = {"", "/paginada"})
    public ResponseEntity<PageResult<LancamentoResponseDTO>> findAll(
            LancamentoFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {

        PageRequest pageRequest = PageRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(lancamentoUseCase.findAll(filter, pageRequest)
                .map(mapper::toDTO));
    }

    // Sobrecarga de conveniência para uso programático e testes unitários
    public ResponseEntity<PageResult<LancamentoResponseDTO>> findAll(
            int page,
            int size,
            LancamentoFilter filter) {
        return findAll(filter, page, size, null, null);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<LancamentoResponseDTO> findById(@PathVariable Long id) {
        Lancamento lancamento = lancamentoUseCase.findById(id);
        return ResponseEntity.ok(mapper.toDTO(lancamento));
    }

    @Override
    @PostMapping
    public ResponseEntity<LancamentoResponseDTO> create(@RequestBody @Valid LancamentoCreateDTO dto) {
        Categoria categoria = categoriaUseCase.findById(dto.categoriaId());
        Pessoa pessoa = pessoaUseCase.findById(dto.pessoaId());

        Lancamento lancamento = lancamentoUseCase.create(
            mapper.toDomain(dto, categoria, pessoa)
        );

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(lancamento.getId())
            .toUri();

        return ResponseEntity.created(location).body(mapper.toDTO(lancamento));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<LancamentoResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid LancamentoUpdateDTO dto) {

        Categoria categoria = categoriaUseCase.findById(dto.categoriaId());
        Pessoa pessoa = pessoaUseCase.findById(dto.pessoaId());

        Lancamento novosDados = mapper.toDomain(dto, categoria, pessoa);
        Lancamento atualizado = lancamentoUseCase.update(id, novosDados);

        return ResponseEntity.ok(mapper.toDTO(atualizado));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lancamentoUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}