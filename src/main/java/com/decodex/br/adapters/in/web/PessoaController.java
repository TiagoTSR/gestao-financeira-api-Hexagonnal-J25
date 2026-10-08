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

    @Override
    @GetMapping(value = {"", "/paginada"})
    public ResponseEntity<PageResult<PessoaResponseDTO>> findAll(
            PessoaFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {

        PageRequest pageRequest = PageRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(inputPort.findAll(filter, pageRequest)
                .map(PessoaResponseDTO::from));
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
    public ResponseEntity<PessoaResponseDTO> findById(@PathVariable Long id) {
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
            @PathVariable Long id,
            @RequestBody @Valid PessoaUpdateDTO dto) {

        Pessoa atualizada = inputPort.update(id, dto.toDomain());

        return ResponseEntity.ok(PessoaResponseDTO.from(atualizada));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inputPort.delete(id);
        return ResponseEntity.noContent().build();
    }
}