package com.decodex.br.testesunitarios.adapters.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.decodex.br.adapters.in.web.exception.DataIntegrityMessageResolver;
import com.decodex.br.adapters.in.web.exception.ErrorResponse;
import com.decodex.br.adapters.in.web.exception.GlobalExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@DisplayName("Testes unitários para GlobalExceptionHandler")
class GlobalExceptionHandlerUnitTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler(new DataIntegrityMessageResolver());
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @ParameterizedTest(name = "[{index}] constraint={0} → \"{1}\"")
    @DisplayName("Deve mapear violações de integridade para mensagens amigáveis")
    @CsvSource({
            // constraint no cause                 | mensagem esperada
            "uk_categoria_tenant_nome, Já existe uma categoria com este nome.",
            "uk_categoria_nome,        Já existe uma categoria com este nome.",
            "fk_lancamento_categoria,  Categoria possui lançamentos vinculados e não pode ser excluída.",
            "fk_lancamento_pessoa,     Pessoa possui lançamentos vinculados e não pode ser excluída.",
            "uk_usuario_email,         Já existe um usuário com este e-mail.",
            "uk_usuario_username,      Já existe um usuário com este nome de usuário."
    })
    void deveMapearViolacaoDeIntegridade(String constraint, String mensagemEsperada) {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("violates " + constraint);

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
    
        assertThat(response.getBody().message()).isEqualTo(mensagemEsperada);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Deve retornar mensagem genérica para constraint não mapeada")
    void deveRetornarMensagemGenericaParaConstraintNaoMapeada() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("other constraint");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message())
                .isEqualTo("Operação não permitida por restrição de integridade de dados.");
    }
}