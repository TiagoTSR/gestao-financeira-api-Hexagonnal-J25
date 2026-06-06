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
        when(categoriaUseCase.findById(1L))
            .thenThrow(new ResourceNotFoundException("Recurso não encontrado no sistema"));

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
        when(categoriaUseCase.findById(2L))
            .thenThrow(new EntityNotFoundException("Erro do JPA"));

        mockMvc.perform(get("/categorias/2"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Deve retornar 400 e ErrorResponse quando lançar IllegalArgumentException")
    void handleIllegalArgument() throws Exception {
        when(categoriaUseCase.findById(3L))
            .thenThrow(new IllegalArgumentException("Parâmetro inválido fornecido"));

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
        // JSON intencionalmente quebrado: faltando fechar aspas e a chave final
        String jsonMalformado = "{ \"nome\": \"Lazer\" ";

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
        String jsonInvalido = "{ \"nome\": \"\" }";

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalido))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.nome").exists());
    }

    @Test
    @DisplayName("Deve retornar 500 genérico para exceções não mapeadas (Exception.class)")
    void handleGenericException() throws Exception {
        when(categoriaUseCase.findById(4L))
            .thenThrow(new NullPointerException("Nulo inesperado"));

        mockMvc.perform(get("/categorias/4"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("Erro inesperado"))
            .andExpect(jsonPath("$.path").value("/categorias/4"));
    }

    @Test
    @DisplayName("Deve retornar 400 e ErrorResponse quando o tipo do parâmetro de path for inválido (MethodArgumentTypeMismatchException)")
    void handleTypeMismatch() throws Exception {
        // O ID da rota não é um número válido (deveria ser Long)
        mockMvc.perform(get("/categorias/abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Valor inválido para o parâmetro 'id'."))
            .andExpect(jsonPath("$.path").value("/categorias/abc"));
    }
}