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

import com.decodex.br.application.dto.pessoa.PessoaCreateDTO;
import com.decodex.br.application.dto.pessoa.PessoaResponseDTO;
import com.decodex.br.application.dto.pessoa.PessoaUpdateDTO;
import com.decodex.br.application.mapper.PessoaDTOMapper;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.port.in.PessoaUseCase;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaUseCase pessoaUseCase;

    public PessoaController(PessoaUseCase pessoaUseCase) {
        this.pessoaUseCase = pessoaUseCase;
    }

    @GetMapping
    public ResponseEntity<List<PessoaResponseDTO>> findAll() {
        List<PessoaResponseDTO> response = pessoaUseCase.findAll()
            .stream()
            .map(PessoaDTOMapper::toDTO)
            .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> findById(@PathVariable Long id) {
        Pessoa pessoa = pessoaUseCase.findById(id);
        return ResponseEntity.ok(PessoaDTOMapper.toDTO(pessoa));
    }

    @PostMapping
    public ResponseEntity<PessoaResponseDTO> create(@RequestBody @Valid PessoaCreateDTO dto) {
        Pessoa pessoa = pessoaUseCase.create(PessoaDTOMapper.toDomain(dto));

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(pessoa.getId())
            .toUri();

        return ResponseEntity.created(location).body(PessoaDTOMapper.toDTO(pessoa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid PessoaUpdateDTO dto) {

        Pessoa novosDados = PessoaDTOMapper.toDomain(dto);
        Pessoa atualizada = pessoaUseCase.update(id, novosDados);

        return ResponseEntity.ok(PessoaDTOMapper.toDTO(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pessoaUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}