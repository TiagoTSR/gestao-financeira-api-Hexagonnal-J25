package com.decodex.br.domain.exeption;

public class RefreshTokenException extends RuntimeException {
    
    public RefreshTokenException(String message) {
        super(message);
    }
}
