package com.decodex.br.testesunitarios.adapters.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import com.decodex.br.adapters.in.web.LancamentoController;
import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoResponseDTO;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.domain.model.*;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.CategoriaUseCase;
import com.decodex.br.domain.port.in.LancamentoUseCase;
import com.decodex.br.domain.port.in.PessoaUseCase;

@ExtendWith(MockitoExtension.class)
class LancamentoControllerUnitarioTest {

    @Mock private LancamentoUseCase lancamentoUseCase;
    @Mock private CategoriaUseCase categoriaUseCase;
    @Mock private PessoaUseCase pessoaUseCase;

    @InjectMocks private LancamentoController controller;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private Endereco enderecoFake() {
        return new Endereco("Rua X", "123", null, "Bairro Y", "00000-000", "Cidade", "SP");
    }

    private Pessoa pessoaFake() {
        return new Pessoa(1L, "João", enderecoFake(), true);
    }

    private Lancamento lancamentoFake() {
        return new Lancamento(1L, "Salário", LocalDate.now(), null, BigDecimal.TEN, null, 
                              TipoLancamento.RECEITA, new Categoria(1L, "Renda"), pessoaFake());
    }

    @Test
    @DisplayName("Deve retornar lista paginada de lançamentos com status 200")
    void findAll_deveRetornar200() {
        PageResult<Lancamento> pageResult = new PageResult<>(
            List.of(lancamentoFake()), 0, 10, 1L, 1
        );
        when(lancamentoUseCase.findAll(any(PageRequest.class))).thenReturn(pageResult);

        PageResult<LancamentoResponseDTO> response = controller.findAll(0, 10);

        assertEquals(1, response.content().size());
        assertEquals(0, response.page());
        assertEquals(10, response.size());
        assertEquals("Salário", response.content().get(0).descricao());
    }

    @Test
    @DisplayName("Deve retornar lançamento por ID com status 200")
    void findById_deveRetornar200() {
        when(lancamentoUseCase.findById(1L)).thenReturn(lancamentoFake());

        ResponseEntity<LancamentoResponseDTO> response = controller.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Salário", response.getBody().descricao());
    }

    @Test
    @DisplayName("Deve criar lançamento e retornar status 201")
    void create_deveRetornar201() {
        LancamentoCreateDTO dto = new LancamentoCreateDTO(
            "Salário", LocalDate.now(), null, BigDecimal.TEN, null, TipoLancamento.RECEITA, 1L, 1L
        );
        when(categoriaUseCase.findById(1L)).thenReturn(new Categoria(1L, "Renda"));
        when(pessoaUseCase.findById(1L)).thenReturn(pessoaFake());
        when(lancamentoUseCase.create(any(Lancamento.class))).thenReturn(lancamentoFake());

        ResponseEntity<LancamentoResponseDTO> response = controller.create(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
    }

    @Test
    @DisplayName("Deve atualizar lançamento e retornar status 200")
    void update_deveRetornar200() {
        LancamentoUpdateDTO dto = new LancamentoUpdateDTO(
            "Salário", LocalDate.now(), null, BigDecimal.TEN, null, TipoLancamento.RECEITA, 1L, 1L
        );
        when(categoriaUseCase.findById(1L)).thenReturn(new Categoria(1L, "Renda"));
        when(pessoaUseCase.findById(1L)).thenReturn(pessoaFake());
        when(lancamentoUseCase.update(eq(1L), any(Lancamento.class))).thenReturn(lancamentoFake());

        ResponseEntity<LancamentoResponseDTO> response = controller.update(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve deletar lançamento e retornar status 204")
    void delete_deveRetornar204() {
        doNothing().when(lancamentoUseCase).delete(1L);
        ResponseEntity<Void> response = controller.delete(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}