package com.decodex.br.domain.pagination;

public record PageRequest(int page, int size) {

    public PageRequest {
        if (page < 0) throw new IllegalArgumentException("Page index must be >= 0, got: " + page);
        if (size < 1) throw new IllegalArgumentException("Page size must be >= 1, got: " + size);
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }
}