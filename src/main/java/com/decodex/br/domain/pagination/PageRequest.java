package com.decodex.br.domain.pagination;

import java.util.Objects;

public final class PageRequest {

    private final int page;
    private final int size;

    public PageRequest(int page, int size) {
        if (page < 0)  throw new IllegalArgumentException("Page index must be >= 0, got: " + page);
        if (size < 1)  throw new IllegalArgumentException("Page size must be >= 1, got: " + size);
        this.page = page;
        this.size = size;
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }

    public int getPage() { return page; }
    public int getSize() { return size; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageRequest that)) return false;
        return page == that.page && size == that.size;
    }

    @Override
    public int hashCode() { return Objects.hash(page, size); }

    @Override
    public String toString() {
        return "PageRequest{page=" + page + ", size=" + size + '}';
    }
}