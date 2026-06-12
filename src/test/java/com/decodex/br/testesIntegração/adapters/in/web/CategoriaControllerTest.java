package com.decodex.br.testesIntegração.adapters.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.decodex.br.adapters.in.web.CategoriaController;
import com.decodex.br.application.dto.categoria.CategoriaCreateDTO;
import com.decodex.br.application.dto.categoria.CategoriaUpdateDTO;
import com.decodex.br.domain.exeption.ResourceNotFoundException;
import com.decodex.br.domain.filter.CategoriaFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.CategoriaUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(controllers = CategoriaController.class)
@DisplayName("Web - CategoriaController")
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private CategoriaUseCase categoriaUseCase;

    @Test
    @DisplayName("Deve retornar 201 Created e o Header Location ao criar categoria")
    void create_DeveRetornar201() throws Exception {
        CategoriaCreateDTO requestDTO = new CategoriaCreateDTO("Lazer");
        Categoria categoriaCriada = new Categoria(1L, "Lazer");

        when(categoriaUseCase.create(any(Categoria.class))).thenReturn(categoriaCriada);

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/categorias/1")))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Lazer"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar criar categoria com nome em branco")
    void create_DeveRetornar400_QuandoNomeInvalido() throws Exception {
        CategoriaCreateDTO requestDTO = new CategoriaCreateDTO("");

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.nome").exists());
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao buscar categoria por ID existente")
    void findById_DeveRetornar200() throws Exception {
        Categoria categoria = new Categoria(1L, "Lazer");
        when(categoriaUseCase.findById(1L)).thenReturn(categoria);

        mockMvc.perform(get("/categorias/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Lazer"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando categoria não existir")
    void findById_DeveRetornar404_QuandoNaoEncontrada() throws Exception {
        when(categoriaUseCase.findById(99L))
            .thenThrow(new ResourceNotFoundException("Categoria não encontrada"));

        mockMvc.perform(get("/categorias/{id}", 99L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e lista paginada de categorias")
    void findAll_DeveRetornar200ELista() throws Exception {
        PageResult<Categoria> pageResult = new PageResult<>(
            List.of(new Categoria(1L, "Lazer"), new Categoria(2L, "Alimentação")),
            0, 10, 2L, 1
        );

        when(categoriaUseCase.findAll(any(CategoriaFilter.class), any(PageRequest.class)))
            .thenReturn(pageResult);

        mockMvc.perform(get("/categorias")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].nome").value("Lazer"))
            .andExpect(jsonPath("$.content[1].nome").value("Alimentação"))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao atualizar categoria")
    void update_DeveRetornar200() throws Exception {
        CategoriaUpdateDTO requestDTO = new CategoriaUpdateDTO("Saúde");
        Categoria categoriaAtualizada = new Categoria(1L, "Saúde");

        when(categoriaUseCase.update(eq(1L), any(Categoria.class))).thenReturn(categoriaAtualizada);

        mockMvc.perform(put("/categorias/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Saúde"));
    }

    @Test
    @DisplayName("Deve retornar 204 No Content ao deletar categoria")
    void delete_DeveRetornar204() throws Exception {
        doNothing().when(categoriaUseCase).delete(1L);

        mockMvc.perform(delete("/categorias/{id}", 1L))
            .andExpect(status().isNoContent());
    }
}