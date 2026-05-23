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
import com.decodex.br.domain.model.Categoria;
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

    // Criamos um "dublê" do UseCase. O Spring injeta esse Mock no CategoriaController
    // Nota: Se estiver usando Spring Boot 3.4+, você pode trocar @MockBean por @MockitoBean
    @MockitoBean
    private CategoriaUseCase categoriaUseCase;

    // -------------------------------------------------------------------------
    // POST /categorias
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar 201 Created e o Header Location ao criar categoria")
    void create_DeveRetornar201() throws Exception {
        CategoriaCreateDTO requestDTO = new CategoriaCreateDTO("Lazer");
        Categoria categoriaCriada = new Categoria(1L, "Lazer"); // Assumindo construtor (Long, String)

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
        // O nome está em branco, violando o @NotBlank do CategoriaCreateDTO
        CategoriaCreateDTO requestDTO = new CategoriaCreateDTO("");

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isBadRequest())
            // Valida se o GlobalExceptionHandler formatou o erro de validação corretamente
            .andExpect(jsonPath("$.nome").exists()); 
    }

    // -------------------------------------------------------------------------
    // GET /categorias/{id}
    // -------------------------------------------------------------------------

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
        // Simulamos o Caso de Uso lançando a exceção de negócio
        when(categoriaUseCase.findById(99L))
            .thenThrow(new ResourceNotFoundException("Categoria não encontrada"));

        mockMvc.perform(get("/categorias/{id}", 99L))
            .andExpect(status().isNotFound())
            // Valida se o GlobalExceptionHandler capturou e montou o ErrorResponse
            .andExpect(jsonPath("$.error").value("Not Found")) 
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    // -------------------------------------------------------------------------
    // GET /categorias
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar 200 OK e lista de categorias")
    void findAll_DeveRetornar200ELista() throws Exception {
        List<Categoria> categorias = List.of(
            new Categoria(1L, "Lazer"),
            new Categoria(2L, "Alimentação")
        );

        when(categoriaUseCase.findAll()).thenReturn(categorias);

        mockMvc.perform(get("/categorias"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].nome").value("Lazer"))
            .andExpect(jsonPath("$[1].nome").value("Alimentação"));
    }

    // -------------------------------------------------------------------------
    // PUT /categorias/{id}
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // DELETE /categorias/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar 204 No Content ao deletar categoria")
    void delete_DeveRetornar204() throws Exception {
        doNothing().when(categoriaUseCase).delete(1L);

        mockMvc.perform(delete("/categorias/{id}", 1L))
            .andExpect(status().isNoContent());
    }
}