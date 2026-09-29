package com.decodex.br.testesunitarios.domain.pagination;

import com.decodex.br.domain.pagination.PageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes unitários para PageRequest")
class PageRequestTest {

    @Nested
    @DisplayName("Validações no construtor compacto")
    class ValidacoesConstrutor {

        @Test
        @DisplayName("Deve criar PageRequest válido com parâmetros mínimos")
        void deveCriarPageRequestValido() {
            PageRequest request = new PageRequest(0, 10);

            assertThat(request.page()).isZero();
            assertThat(request.size()).isEqualTo(10);
            assertThat(request.sort()).isNull();
            assertThat(request.direction()).isNull();
            assertThat(request.hasSort()).isFalse();
            assertThat(request.isAsc()).isTrue();
            assertThat(request.isDesc()).isFalse();
            assertThat(request.offset()).isZero();
        }

        @Test
        @DisplayName("Deve lançar exceção quando índice da página for negativo")
        void deveLancarExcecaoQuandoPaginaNegativa() {
            assertThatThrownBy(() -> new PageRequest(-1, 10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("O índice da página deve ser >= 0, informado: -1");
        }

        @Test
        @DisplayName("Deve lançar exceção quando tamanho da página for menor que 1")
        void deveLancarExcecaoQuandoTamanhoMenorQueUm() {
            assertThatThrownBy(() -> new PageRequest(0, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("O tamanho da página deve ser >= 1, informado: 0");
        }

        @Test
        @DisplayName("Deve lançar exceção quando tamanho exceder o limite máximo")
        void deveLancarExcecaoQuandoTamanhoExcederLimite() {
            assertThatThrownBy(() -> new PageRequest(0, 101))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("O tamanho da página não pode exceder 100, informado: 101");
        }

        @Test
        @DisplayName("Deve sanitizar strings vazias ou em branco para null")
        void deveSanitizarStringsEmBrancoParaNull() {
            PageRequest request = new PageRequest(1, 20, "   ", "   ");

            assertThat(request.sort()).isNull();
            assertThat(request.direction()).isNull();
            assertThat(request.hasSort()).isFalse();
        }

        @Test
        @DisplayName("Deve fazer trim e normalizar campos válidos")
        void deveFazerTrimENormalizarCampos() {
            PageRequest request = new PageRequest(0, 15, "  nome  ", "  desc  ");

            assertThat(request.sort()).isEqualTo("nome");
            assertThat(request.direction()).isEqualTo("DESC");
        }
    }

    @Nested
    @DisplayName("Direção e Ordenação (Sort & Direction)")
    class OrdenacaoEDirecao {

        @ParameterizedTest
        @ValueSource(strings = {"desc", "DESC", "Desc", " desc "})
        @DisplayName("Deve identificar ordenação descendente quando direction for 'desc'")
        void deveIdentificarDescendentePorDirection(String direction) {
            PageRequest request = PageRequest.of(0, 10, "nome", direction);

            assertThat(request.isDesc()).isTrue();
            assertThat(request.isAsc()).isFalse();
            assertThat(request.getDirection()).isEqualTo(PageRequest.Direction.DESC);
        }

        @ParameterizedTest
        @ValueSource(strings = {"nome,desc", "nome,DESC", "nome desc", "desc"})
        @DisplayName("Deve identificar ordenação descendente quando o sort embutir ',desc' ou ' desc'")
        void deveIdentificarDescendenteEmbutidoNoSort(String sort) {
            PageRequest request = PageRequest.of(0, 10, sort);

            assertThat(request.isDesc()).isTrue();
            assertThat(request.isAsc()).isFalse();
            assertThat(request.getDirection()).isEqualTo(PageRequest.Direction.DESC);
        }

        @Test
        @DisplayName("Deve retornar ascendente por padrão quando nada for informado")
        void deveRetornarAscendentePorPadrao() {
            PageRequest request = PageRequest.of(0, 10, "nome", "asc");

            assertThat(request.isAsc()).isTrue();
            assertThat(request.isDesc()).isFalse();
            assertThat(request.getDirection()).isEqualTo(PageRequest.Direction.ASC);
        }

        @Test
        @DisplayName("Deve extrair a propriedade limpa mesmo quando o sort vier com ',desc' ou ' asc'")
        void deveExtrairPropriedadeLimpa() {
            PageRequest req1 = PageRequest.of(0, 10, "nome,desc");
            PageRequest req2 = PageRequest.of(0, 10, "dataVencimento asc");
            PageRequest req3 = PageRequest.of(0, 10, "valor");
            PageRequest req4 = PageRequest.of(0, 10);

            assertThat(req1.getSortProperty()).contains("nome");
            assertThat(req2.getSortProperty()).contains("dataVencimento");
            assertThat(req3.getSortProperty()).contains("valor");
            assertThat(req4.getSortProperty()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Cálculo de offset e navegação fluente")
    class OffsetENavegacao {

        @Test
        @DisplayName("Deve calcular offset corretamente")
        void deveCalcularOffset() {
            assertThat(PageRequest.of(0, 10).offset()).isZero();
            assertThat(PageRequest.of(1, 10).offset()).isEqualTo(10L);
            assertThat(PageRequest.of(3, 25).offset()).isEqualTo(75L);
        }

        @Test
        @DisplayName("Deve navegar para próxima página preservando ordenação")
        void deveNavegarParaProximaPagina() {
            PageRequest current = PageRequest.of(1, 20, "nome", "DESC");
            PageRequest next = current.next();

            assertThat(next.page()).isEqualTo(2);
            assertThat(next.size()).isEqualTo(20);
            assertThat(next.sort()).isEqualTo("nome");
            assertThat(next.direction()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("Deve navegar para página anterior respeitando o limite da página 0")
        void deveNavegarParaPaginaAnterior() {
            PageRequest page2 = PageRequest.of(2, 10);
            PageRequest page1 = page2.previousOrFirst();
            PageRequest page0 = page1.previousOrFirst();
            PageRequest stillPage0 = page0.previousOrFirst();

            assertThat(page1.page()).isEqualTo(1);
            assertThat(page0.page()).isZero();
            assertThat(stillPage0.page()).isZero();
            assertThat(stillPage0).isSameAs(page0);
        }

        @Test
        @DisplayName("Deve retornar para a primeira página")
        void deveRetornarParaPrimeiraPagina() {
            PageRequest req = PageRequest.of(5, 15, "descricao", PageRequest.Direction.ASC);
            PageRequest first = req.first();

            assertThat(first.page()).isZero();
            assertThat(first.size()).isEqualTo(15);
            assertThat(first.sort()).isEqualTo("descricao");
        }

        @Test
        @DisplayName("Deve criar página padrão com valores esperados")
        void deveCriarPaginaPadrao() {
            PageRequest def = PageRequest.defaultPage();

            assertThat(def.page()).isEqualTo(PageRequest.DEFAULT_PAGE);
            assertThat(def.size()).isEqualTo(PageRequest.DEFAULT_SIZE);
        }
    }
}
