package com.decodex.br.domain.pagination;

import java.util.Locale;
import java.util.Optional;

/**
 * Representa os parâmetros de paginação e ordenação no domínio da aplicação.
 * É um record imutável, defensivo e compatível com consultas paginadas e frameworks ORM.
 */
public record PageRequest(
        int page,
        int size,
        String sort,
        String direction
) {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 100;

    public enum Direction {
        ASC, DESC;

        public static Direction fromString(String value) {
            if (value == null || value.isBlank()) {
                return ASC;
            }
            return "desc".equalsIgnoreCase(value.trim()) ? DESC : ASC;
        }

        public boolean isAscending() {
            return this == ASC;
        }

        public boolean isDescending() {
            return this == DESC;
        }
    }

    /**
     * Construtor canônico compacto com validação defensiva e sanitização de strings.
     */
    public PageRequest {
        if (page < 0) {
            throw new IllegalArgumentException("O índice da página deve ser >= 0, informado: " + page);
        }
        if (size < 1) {
            throw new IllegalArgumentException("O tamanho da página deve ser >= 1, informado: " + size);
        }
        if (size > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "O tamanho da página não pode exceder " + MAX_SIZE + ", informado: " + size);
        }

        // Sanitiza espaços em branco e strings vazias para null
        sort = (sort != null && !sort.isBlank()) ? sort.trim() : null;
        direction = (direction != null && !direction.isBlank()) ? direction.trim().toUpperCase(Locale.ROOT) : null;
    }

    /**
     * Construtor de conveniência para paginação básica sem ordenação.
     */
    public PageRequest(int page, int size) {
        this(page, size, null, null);
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, null, null);
    }

    public static PageRequest of(int page, int size, String sort) {
        return new PageRequest(page, size, sort, null);
    }

    public static PageRequest of(int page, int size, String sort, String direction) {
        return new PageRequest(page, size, sort, direction);
    }

    public static PageRequest of(int page, int size, String sort, Direction direction) {
        return new PageRequest(page, size, sort, direction != null ? direction.name() : null);
    }

    public static PageRequest defaultPage() {
        return new PageRequest(DEFAULT_PAGE, DEFAULT_SIZE, null, null);
    }

    /**
     * Calcula o offset de paginação (quantidade de registros a pular).
     * Útil para queries JPQL, SQL nativo ou integração com Specifications.
     */
    public long offset() {
        return (long) page * size;
    }

    /**
     * Indica se uma propriedade de ordenação foi especificada.
     */
    public boolean hasSort() {
        return sort != null && !sort.isBlank();
    }

    /**
     * Extrai com segurança apenas o nome da propriedade/coluna de ordenação,
     * removendo eventuais sufixos como ',desc', ',asc', ' desc' ou ' asc'.
     *
     * Isso impede erros de runtime no Spring Data/JPA como:
     * "PropertyReferenceException: No property 'nome,desc' found for type..."
     */
    public Optional<String> getSortProperty() {
        if (!hasSort()) {
            return Optional.empty();
        }
        String cleanSort = sort;
        int commaIndex = cleanSort.indexOf(',');
        if (commaIndex != -1) {
            cleanSort = cleanSort.substring(0, commaIndex).trim();
        } else {
            int spaceIndex = cleanSort.indexOf(' ');
            if (spaceIndex != -1) {
                cleanSort = cleanSort.substring(0, spaceIndex).trim();
            }
        }
        return cleanSort.isBlank() ? Optional.empty() : Optional.of(cleanSort);
    }

    /**
     * Retorna a direção tipada como Direction enum.
     */
    public Direction getDirection() {
        return isDesc() ? Direction.DESC : Direction.ASC;
    }

    /**
     * Identifica se a ordenação deve ser descendente,
     * analisando tanto o campo direction quanto formatos combinados em sort (ex: "nome,desc").
     */
    public boolean isDesc() {
        if (direction != null && !direction.isBlank()) {
            return "desc".equalsIgnoreCase(direction.trim());
        }
        if (sort != null && !sort.isBlank()) {
            String s = sort.trim().toLowerCase(Locale.ROOT);
            return s.endsWith(",desc") || s.endsWith(" desc") || s.equals("desc");
        }
        return false;
    }

    public boolean isAsc() {
        return !isDesc();
    }

    /**
     * Gera a próxima página mantendo a mesma configuração de tamanho e ordenação.
     */
    public PageRequest next() {
        return new PageRequest(page + 1, size, sort, direction);
    }

    /**
     * Gera a página anterior (ou a primeira, caso já esteja na página 0).
     */
    public PageRequest previousOrFirst() {
        return page == 0 ? this : new PageRequest(page - 1, size, sort, direction);
    }

    /**
     * Gera a primeira página (page 0) mantendo as mesmas opções.
     */
    public PageRequest first() {
        return new PageRequest(0, size, sort, direction);
    }
}