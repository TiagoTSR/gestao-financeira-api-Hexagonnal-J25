package com.decodex.br.testesunitarios.adapters.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.decodex.br.adapters.in.web.exception.ErrorResponse;
import com.decodex.br.adapters.in.web.exception.GlobalExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@DisplayName("Testes unitários para GlobalExceptionHandler")
class GlobalExceptionHandlerUnitTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    @DisplayName("Deve tratar violação de constraint de categoria tenant com mensagem amigável")
    void deveTratarConstraintCategoriaTenant() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("violates uk_categoria_tenant_nome");
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Já existe uma categoria cadastrada com este nome.");
    }

    @Test
    @DisplayName("Deve tratar violação de FK de exclusão de categoria com mensagem amigável")
    void deveTratarConstraintFkLancamentoCategoria() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("violates foreign key fk_lancamento_categoria");
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Não é possível excluir esta categoria pois existem lançamentos financeiros vinculados a ela.");
    }

    @Test
    @DisplayName("Deve tratar violação de FK de exclusão de pessoa com mensagem amigável")
    void deveTratarConstraintFkLancamentoPessoa() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("violates foreign key fk_lancamento_pessoa");
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Não é possível excluir esta pessoa pois existem lançamentos financeiros vinculados a ela.");
    }

    @Test
    @DisplayName("Deve tratar violação de unicidade de email de usuário")
    void deveTratarConstraintUsuarioEmail() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("violates uk_usuario_email");
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Já existe um usuário cadastrado com este e-mail.");
    }

    @Test
    @DisplayName("Deve tratar violação de integridade genérica")
    void deveTratarViolacaoGenerica() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("other constraint");
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Operação não permitida: violação de integridade de dados.");
    }
}
