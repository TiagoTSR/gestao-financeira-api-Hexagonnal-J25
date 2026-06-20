package com.decodex.br.domain.exeption;

public class RelatorioPdfException extends RuntimeException {

    public RelatorioPdfException(String message) {
        super(message);
    }

    public RelatorioPdfException(String message, Throwable cause) {
        super(message, cause);
    }
}