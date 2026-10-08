package com.decodex.br.testesunitarios.adapters.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import java.util.UUID;

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

import com.decodex.br.adapters.in.web.CategoriaController;
import com.decodex.br.application.dto.categoria.CategoriaCreateDTO;
import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.categoria.CategoriaUpdateDTO;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import com.decodex.br.application.dto.categoria.CategoriaFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.CategoriaInputPort;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerUnitarioTest {

    @Mock
    private CategoriaInputPort categoriaInputPort;

    @InjectMocks
    private CategoriaController controller;

    private final UUID idPadrao = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("Deve retornar lista paginada de categorias com status 200")
    void findAll_deveRetornar200() {
        PageResult<Categoria> pageResult = new PageResult<>(
            List.of(new Categoria(idPadrao, "Lazer")), 0, 10, 1L, 1
        );

        when(categoriaInputPort.findAll(any(CategoriaFilter.class), any(PageRequest.class)))
            .thenReturn(pageResult);

        CategoriaFilter filter = new CategoriaFilter();
        ResponseEntity<PageResult<CategoriaResponseDTO>> response = controller.findAll(0, 10, filter);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        assertEquals(0, response.getBody().page());
        assertEquals(10, response.getBody().size());
        assertEquals("Lazer", response.getBody().content().get(0).nome());
    }

    @Test
    @DisplayName("Deve retornar categoria por ID com status 200")
    void findById_deveRetornar200() {
        doReturn(new Categoria(idPadrao, "Lazer")).when(categoriaInputPort).findById(idPadrao);

        ResponseEntity<CategoriaResponseDTO> response = controller.findById(idPadrao);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(idPadrao, response.getBody().id());
    }

    @Test
    @DisplayName("Deve repassar exceção quando categoria não for encontrada")
    void findById_deveLancarExcecao() {
        UUID idInexistente = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Erro")).when(categoriaInputPort).findById(idInexistente);

        assertThrows(ResourceNotFoundException.class, () -> controller.findById(idInexistente));
    }

    @Test
    @DisplayName("Deve criar categoria e retornar status 201")
    void create_deveRetornar201() {
        CategoriaCreateDTO dto = new CategoriaCreateDTO("Lazer");
        doReturn(new Categoria(idPadrao, "Lazer")).when(categoriaInputPort).create(any(Categoria.class));

        ResponseEntity<CategoriaResponseDTO> response = controller.create(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertEquals(idPadrao, response.getBody().id());
    }

    @Test
    @DisplayName("Deve atualizar categoria e retornar status 200")
    void update_deveRetornar200() {
        CategoriaUpdateDTO dto = new CategoriaUpdateDTO("Atualizado");
        doReturn(new Categoria(idPadrao, "Atualizado")).when(categoriaInputPort).update(eq(idPadrao), any(Categoria.class));

        ResponseEntity<CategoriaResponseDTO> response = controller.update(idPadrao, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Atualizado", response.getBody().nome());
    }

    @Test
    @DisplayName("Deve deletar categoria e retornar status 204")
    void delete_deveRetornar204() {
        doNothing().when(categoriaInputPort).delete(idPadrao);

        ResponseEntity<Void> response = controller.delete(idPadrao);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}