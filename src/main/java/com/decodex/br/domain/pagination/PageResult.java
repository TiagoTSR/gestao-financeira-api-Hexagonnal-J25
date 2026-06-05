package com.decodex.br.domain.pagination;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PageResult<T> {

    private final List<T> content;
    private final int    page;
    private final int    size;
    private final long   totalElements;
    private final int    totalPages;

    public PageResult(List<T> content, int page, int size,
                      long totalElements, int totalPages) {
        Objects.requireNonNull(content, "content must not be null");
        this.content       = Collections.unmodifiableList(content);
        this.page          = page;
        this.size          = size;
        this.totalElements = totalElements;
        this.totalPages    = totalPages;
    }

    public List<T> getContent()          { return content; }
    public int    getPage()              { return page; }
    public int    getSize()              { return size; }
    public long   getTotalElements()     { return totalElements; }
    public int    getTotalPages()        { return totalPages; }

    public boolean isEmpty()   { return content.isEmpty(); }

    public boolean hasNext()   { return page < totalPages - 1; }

    public boolean hasPrevious() { return page > 0; }

    public boolean isFirst()   { return page == 0; }

    public boolean isLast()    { return !hasNext(); }

    public <R> PageResult<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        List<R> mapped = content.stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new PageResult<>(mapped, page, size, totalElements, totalPages);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageResult<?> that)) return false;
        return page == that.page
            && size == that.size
            && totalElements == that.totalElements
            && totalPages == that.totalPages
            && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, page, size, totalElements, totalPages);
    }

    @Override
    public String toString() {
        return "PageResult{page=" + page
            + ", size=" + size
            + ", totalElements=" + totalElements
            + ", totalPages=" + totalPages
            + ", content.size=" + content.size()
            + '}';
    }
}