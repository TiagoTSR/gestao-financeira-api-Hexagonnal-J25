package com.decodex.br.adapters.out.persistence.specification;

/**
 * Utilitário para construção de predicados e tratamento defensivo em JPA Specifications.
 */
public final class SpecificationHelper {

    public static final char ESCAPE_CHAR = '\\';

    private SpecificationHelper() {
    }

    /**
     * Escapa caracteres especiais do operador LIKE SQL:
     * - '\' (escape char) -> '\\'
     * - '%' (qualquer sequência de caracteres) -> '\%'
     * - '_' (qualquer caractere único) -> '\_'
     *
     * @param valor texto de entrada
     * @return texto com caracteres especiais devidamente escapados
     */
    public static String escaparLike(String valor) {
        if (valor == null) {
            return null;
        }
        return valor
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * Formata um valor de filtro para consulta parcial LIKE com caracteres escapados e minúsculos:
     * ex: "10%" -> "%10\%%"
     *
     * @param valor texto de entrada
     * @return padrão pronto para uso com cb.like(..., pattern, ESCAPE_CHAR)
     */
    public static String formatarLike(String valor) {
        if (valor == null) {
            return null;
        }
        return "%" + escaparLike(valor.trim().toLowerCase()) + "%";
    }
}
