package com.decodex.br.domain.exception;

public class RelatorioPdfException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RelatorioPdfException(String message) {
        super(message);
    }

    public RelatorioPdfException(String message, Throwable cause) {
        super(message, cause);
    }
}
