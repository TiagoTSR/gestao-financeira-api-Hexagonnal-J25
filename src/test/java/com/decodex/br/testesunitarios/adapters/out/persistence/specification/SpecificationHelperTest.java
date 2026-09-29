package com.decodex.br.testesunitarios.adapters.out.persistence.specification;

import com.decodex.br.adapters.out.persistence.specification.SpecificationHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes unitários para SpecificationHelper (Escape LIKE SQL)")
class SpecificationHelperTest {

    @Nested
    @DisplayName("Método escaparLike")
    class EscaparLikeTest {

        @Test
        @DisplayName("Deve retornar null quando valor de entrada for null")
        void deveRetornarNullParaEntradaNula() {
            assertThat(SpecificationHelper.escaparLike(null)).isNull();
        }

        @Test
        @DisplayName("Deve retornar string vazia quando entrada for vazia")
        void deveRetornarStringVazia() {
            assertThat(SpecificationHelper.escaparLike("")).isEmpty();
        }

        @ParameterizedTest(name = "Entrada: ''{0}'' -> Saída esperada: ''{1}''")
        @CsvSource({
                "normal, normal",
                "100%, 100\\%",
                "conta_corrente, conta\\_corrente",
                "c:\\arquivos, c:\\\\arquivos",
                "10%_off, 10\\%\\_off",
                "%_\\, \\%\\_\\\\"
        })
        @DisplayName("Deve escapar corretamente caracteres especiais SQL LIKE (%, _ e \\)")
        void deveEscaparCaracteresEspeciais(String entrada, String esperado) {
            assertThat(SpecificationHelper.escaparLike(entrada)).isEqualTo(esperado);
        }
    }

    @Nested
    @DisplayName("Método formatarLike")
    class FormatarLikeTest {

        @Test
        @DisplayName("Deve retornar null quando entrada for null")
        void deveRetornarNull() {
            assertThat(SpecificationHelper.formatarLike(null)).isNull();
        }

        @Test
        @DisplayName("Deve formatar texto com wildcards e trim/lowercase")
        void deveFormatarTextoNormal() {
            assertThat(SpecificationHelper.formatarLike("  Alimentacao  ")).isEqualTo("%alimentacao%");
        }

        @Test
        @DisplayName("Deve formatar texto com caracteres especiais escapados e wildcards")
        void deveFormatarTextoComCaracteresEspeciais() {
            assertThat(SpecificationHelper.formatarLike("  Taxa 10%_EXTRA  ")).isEqualTo("%taxa 10\\%\\_extra%");
        }
    }
}
