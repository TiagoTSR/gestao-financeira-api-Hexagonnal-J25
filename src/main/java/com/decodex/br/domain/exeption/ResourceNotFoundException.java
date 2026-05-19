package com.decodex.br.domain.exeption;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -8394067244217294715L;

	public ResourceNotFoundException(String message) {
        super(message);
    }
}