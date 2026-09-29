package com.decodex.br.testesunitarios.adapters.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.decodex.br.adapters.in.web.PessoaController;
import com.decodex.br.application.dto.pessoa.PessoaCreateDTO;
import com.decodex.br.application.dto.pessoa.PessoaResponseDTO;
import com.decodex.br.application.dto.pessoa.PessoaUpdateDTO;
import com.decodex.br.domain.filter.PessoaFilter;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.PessoaUseCase;

@ExtendWith(MockitoExtension.class)
class PessoaControllerUnitarioTest {

    @Mock
    private PessoaUseCase pessoaUseCase;

    @InjectMocks
    private PessoaController controller;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private Pessoa pessoaFake() {
        Endereco endereco = new Endereco("Rua X", "123", null, "Bairro Y", "00000", "Cidade", "SP");
        return new Pessoa(1L, "João", endereco, true);
    }

    @Test
    @DisplayName("Deve retornar lista paginada de pessoas com status 200")
    void findAll_deveRetornar200() {
        PageResult<Pessoa> pageResult = new PageResult<>(
            List.of(pessoaFake()), 0, 10, 1L, 1
        );
        when(pessoaUseCase.findAll(any(PessoaFilter.class), any(PageRequest.class)))
            .thenReturn(pageResult);

        PessoaFilter filter = new PessoaFilter();
        PageResult<PessoaResponseDTO> response = controller.findAll(filter, 0, 10);

        assertEquals(1, response.content().size());
        assertEquals(0, response.page());
        assertEquals(10, response.size());
        assertEquals("João", response.content().get(0).nome());
    }

    @Test
    @DisplayName("Deve retornar pessoa por ID com status 200")
    void findById_deveRetornar200() {
        when(pessoaUseCase.findById(1L)).thenReturn(pessoaFake());

        ResponseEntity<PessoaResponseDTO> response = controller.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("João", response.getBody().nome());
    }

    @Test
    @DisplayName("Deve criar pessoa e retornar status 201")
    void create_deveRetornar201() {
        PessoaCreateDTO dto = new PessoaCreateDTO(
            "João", "Rua X", "123", null, "Bairro Y", "00000", "Cidade", "SP", true
        );
        when(pessoaUseCase.create(any(Pessoa.class))).thenReturn(pessoaFake());

        ResponseEntity<PessoaResponseDTO> response = controller.create(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
    }

    @Test
    @DisplayName("Deve atualizar pessoa e retornar status 200")
    void update_deveRetornar200() {
        PessoaUpdateDTO dto = new PessoaUpdateDTO(
            "João", "Rua X", "123", null, "Bairro Y", "00000", "Cidade", "SP", true
        );
        when(pessoaUseCase.update(eq(1L), any(Pessoa.class))).thenReturn(pessoaFake());

        ResponseEntity<PessoaResponseDTO> response = controller.update(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve deletar pessoa e retornar status 204")
    void delete_deveRetornar204() {
        doNothing().when(pessoaUseCase).delete(1L);

        ResponseEntity<Void> response = controller.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}