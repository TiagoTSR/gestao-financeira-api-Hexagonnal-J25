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
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.LancamentoInputPort;
import com.decodex.br.adapters.in.web.documentation.LancamentoControllerDoc;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController implements LancamentoControllerDoc {

    private final LancamentoInputPort lancamentoInputPort;
    private final LancamentoDTOMapper mapper;

    public LancamentoController(
            LancamentoInputPort lancamentoInputPort,
            LancamentoDTOMapper mapper) {
        this.lancamentoInputPort = lancamentoInputPort;
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
        return ResponseEntity.ok(lancamentoInputPort.findAll(filter, pageRequest)
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
        Lancamento lancamento = lancamentoInputPort.findById(id);
        return ResponseEntity.ok(mapper.toDTO(lancamento));
    }

    @Override
    @PostMapping
    public ResponseEntity<LancamentoResponseDTO> create(@RequestBody @Valid LancamentoCreateDTO dto) {
        Lancamento lancamento = lancamentoInputPort.create(dto);

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

        Lancamento atualizado = lancamentoInputPort.update(id, dto);

        return ResponseEntity.ok(mapper.toDTO(atualizado));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lancamentoInputPort.delete(id);
        return ResponseEntity.noContent().build();
    }
}