package com.decodex.br.testesunitarios.config.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.decodex.br.config.ratelimit.RateLimitingFilter;
import java.io.IOException;

@DisplayName("Testes unitários - RateLimitingFilter")
class RateLimitingFilterTest {

    @Test
    @DisplayName("Deve permitir requisições dentro do limite e incluir cabeçalhos do Rate Limit")
    void devePermitirRequisicaoDentroDoLimite() throws ServletException, IOException {
        RateLimitingFilter filter = new RateLimitingFilter(2, 1);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getHeader("X-Rate-Limit-Limit")).isEqualTo("2");
        assertThat(response.getHeader("X-Rate-Limit-Remaining")).isEqualTo("1");
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve retornar HTTP 429 quando exceder o limite máximo configurado")
    void deveRetornar429QuandoEstourarLimite() throws ServletException, IOException {
        RateLimitingFilter filter = new RateLimitingFilter(2, 1);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");
        FilterChain filterChain = mock(FilterChain.class);

        // Primeira requisição (sucesso)
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        filter.doFilter(request, response1, filterChain);
        assertThat(response1.getStatus()).isEqualTo(200);

        // Segunda requisição (sucesso)
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        filter.doFilter(request, response2, filterChain);
        assertThat(response2.getStatus()).isEqualTo(200);

        // Terceira requisição (bloqueio 429)
        MockHttpServletResponse response3 = new MockHttpServletResponse();
        filter.doFilter(request, response3, filterChain);

        assertThat(response3.getStatus()).isEqualTo(429);
        assertThat(response3.getHeader("X-Rate-Limit-Limit")).isEqualTo("2");
        assertThat(response3.getHeader("X-Rate-Limit-Remaining")).isEqualTo("0");
        assertThat(response3.getHeader("X-Rate-Limit-Retry-After-Seconds")).isNotNull();
        assertThat(response3.getContentAsString()).contains("Limite de requisições excedido");
        verify(filterChain, times(2)).doFilter(any(), any()); // Apenas as duas primeiras prosseguiram
    }

    @Test
    @DisplayName("Deve usar o IP do cabeçalho X-Forwarded-For se presente")
    void deveUsarIpDoCabecalhoXForwardedFor() throws ServletException, IOException {
        RateLimitingFilter filter = new RateLimitingFilter(1, 1);
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        request1.addHeader("X-Forwarded-For", "203.0.113.195, 70.41.3.18");
        FilterChain filterChain = mock(FilterChain.class);

        // IP 1: Consome limite
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        filter.doFilter(request1, response1, filterChain);
        assertThat(response1.getStatus()).isEqualTo(200);

        // IP 1 novamente: Bloqueado
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        filter.doFilter(request1, response2, filterChain);
        assertThat(response2.getStatus()).isEqualTo(429);

        // IP 2 (Diferente): Liberado
        MockHttpServletRequest request2 = new MockHttpServletRequest();
        request2.addHeader("X-Forwarded-For", "198.51.100.1");
        MockHttpServletResponse response3 = new MockHttpServletResponse();
        filter.doFilter(request2, response3, filterChain);
        assertThat(response3.getStatus()).isEqualTo(200);
    }
}
