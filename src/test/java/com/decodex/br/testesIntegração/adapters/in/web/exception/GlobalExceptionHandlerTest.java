package com.decodex.br.testesIntegração.adapters.in.web.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.decodex.br.adapters.in.web.CategoriaController;
import com.decodex.br.domain.exeption.ResourceNotFoundException;
import com.decodex.br.domain.port.in.CategoriaUseCase;

import jakarta.persistence.EntityNotFoundException;

// Usamos o CategoriaController apenas como "isca" para disparar as exceções
@WebMvcTest(CategoriaController.class)
@DisplayName("Web - GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaUseCase categoriaUseCase;

    @Test
    @DisplayName("Deve retornar 404 e ErrorResponse quando lançar ResourceNotFoundException")
    void handleResourceNotFound() throws Exception {
        // Arrange: Forçamos o UseCase a lançar a exceção de negócio
        when(categoriaUseCase.findById(1L))
            .thenThrow(new ResourceNotFoundException("Recurso não encontrado no sistema"));

        // Act & Assert
        mockMvc.perform(get("/categorias/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Recurso não encontrado no sistema"))
            .andExpect(jsonPath("$.path").value("/categorias/1"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve retornar 404 sem corpo quando lançar JPA EntityNotFoundException")
    void handleEntityNotFound() throws Exception {
        // Arrange
        when(categoriaUseCase.findById(2L))
            .thenThrow(new EntityNotFoundException("Erro do JPA"));

        // Act & Assert: O seu handler atual retorna build() vazio para essa exceção
        mockMvc.perform(get("/categorias/2"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Deve retornar 400 e ErrorResponse quando lançar IllegalArgumentException")
    void handleIllegalArgument() throws Exception {
        // Arrange
        when(categoriaUseCase.findById(3L))
            .thenThrow(new IllegalArgumentException("Parâmetro inválido fornecido"));

        // Act & Assert
        mockMvc.perform(get("/categorias/3"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Parâmetro inválido fornecido"))
            .andExpect(jsonPath("$.path").value("/categorias/3"));
    }

    @Test
    @DisplayName("Deve retornar 400 e mensagem customizada quando enviar JSON malformado (HttpMessageNotReadableException)")
    void handleMessageNotReadable() throws Exception {
        // Arrange: Um JSON quebrado (faltando fechar aspas e chaves)
        String jsonMalformado = "{ \"nome\": \"Lazer "; 

        // Act & Assert: O Spring tenta ler o JSON no POST e lança HttpMessageNotReadableException
        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMalformado))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Requisição inválida. Verifique o formato do JSON ou os valores dos campos."))
            .andExpect(jsonPath("$.path").value("/categorias"));
    }

    @Test
    @DisplayName("Deve retornar 400 e Map de erros quando falhar na validação (@Valid)")
    void handleValidationExceptions() throws Exception {
        // Arrange: JSON com nome vazio para acionar o @NotBlank do DTO
        String jsonInvalido = "{ \"nome\": \"\" }";

        // Act & Assert
        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalido))
            .andExpect(status().isBadRequest())
            // O seu handler de MethodArgumentNotValidException devolve um Map direto, não o ErrorResponse
            .andExpect(jsonPath("$.nome").exists());
    }

    @Test
    @DisplayName("Deve retornar 500 genérico para exceções não mapeadas (Exception.class)")
    void handleGenericException() throws Exception {
        // Arrange: Forçamos um erro genérico não mapeado, como NullPointerException
        when(categoriaUseCase.findById(4L))
            .thenThrow(new NullPointerException("Nulo inesperado"));

        // Act & Assert
        mockMvc.perform(get("/categorias/4"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            // Garante que a mensagem original ("Nulo inesperado") e a stacktrace não vazem!
            .andExpect(jsonPath("$.message").value("Erro inesperado"))
            .andExpect(jsonPath("$.path").value("/categorias/4"));
    }
}