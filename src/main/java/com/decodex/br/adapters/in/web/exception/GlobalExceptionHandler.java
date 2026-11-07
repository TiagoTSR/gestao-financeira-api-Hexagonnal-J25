package com.decodex.br.adapters.in.web.exception;

import com.decodex.br.domain.exception.RefreshTokenException;
import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.exception.RelatorioPdfException;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final DataIntegrityMessageResolver dataIntegrityMessageResolver;

    // ✅ Injeção via construtor (Spring resolve o @Component automaticamente)
    public GlobalExceptionHandler(DataIntegrityMessageResolver dataIntegrityMessageResolver) {
        this.dataIntegrityMessageResolver = dataIntegrityMessageResolver;
    }

    // ---------- 4xx: erros esperados (log DEBUG/WARN, sem stack trace) ----------

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(RegraDeNegocioException ex, HttpServletRequest req) {
        log.debug("Regra de negócio violada em {}: {}", req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        log.debug("Recurso não encontrado em {}: {}", req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Recurso não encontrado.", req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        log.debug("Argumento inválido em {}: {}", req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, safe(ex.getMessage(), "Requisição inválida."), req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest req) {
        String msg = "Valor inválido para o parâmetro '%s'.".formatted(ex.getName());
        return build(HttpStatus.BAD_REQUEST, msg, req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido ou malformado.", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest req) {
        List<ErrorResponse.FieldValidationError> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .toList();

        var body = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Erro de validação nos campos informados.",
                req.getRequestURI(),
                details);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest req) {
        List<ErrorResponse.FieldValidationError> details = ex.getConstraintViolations().stream()
                .map(v -> new ErrorResponse.FieldValidationError(
                        v.getPropertyPath().toString(), v.getMessage()))
                .toList();

        var body = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Erro de validação nos parâmetros informados.",
                req.getRequestURI(),
                details);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                             HttpServletRequest req) {
        // ✅ getMostSpecificCause() nunca retorna null — check removido
        String cause = ex.getMostSpecificCause().getMessage();

        String message = dataIntegrityMessageResolver.resolve(cause);
        if (message == null) {
            log.warn("Violação de integridade não mapeada em {}: {}", req.getRequestURI(), cause);
            message = "Operação não permitida por restrição de integridade de dados.";
        }
        return build(HttpStatus.CONFLICT, message, req);
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleRefreshToken(RefreshTokenException ex, HttpServletRequest req) {
        log.debug("Refresh token inválido em {}: {}", req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, "Sessão expirada ou inválida.", req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        log.debug("Falha de autenticação em {}: {}", req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.", req);
    }

    @ExceptionHandler(RelatorioPdfException.class)
    public ResponseEntity<ErrorResponse> handleRelatorioPdf(RelatorioPdfException ex, HttpServletRequest req) {
        log.error("Erro ao gerar relatório em {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível gerar o relatório.", req);
    }

    // ---------- 5xx: erros inesperados (log ERROR + stack trace, resposta genérica) ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado em {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno. Tente novamente mais tarde.", req);
    }

    // ---------- Helpers ----------

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, req.getRequestURI()));
    }

    private ErrorResponse.FieldValidationError toFieldError(FieldError fe) {
        String msg = fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor inválido.";
        return new ErrorResponse.FieldValidationError(fe.getField(), msg);
    }

    private String safe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}