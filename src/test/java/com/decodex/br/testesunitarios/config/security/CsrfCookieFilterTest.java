package com.decodex.br.testesunitarios.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import com.decodex.br.config.security.CsrfCookieFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class CsrfCookieFilterTest {

    @Test
    void deveExporTokenCsrfNoHeaderParaClienteCrossOrigin() throws ServletException, IOException {
        CsrfToken token = new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "csrf-token-value");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainExecuted = new AtomicBoolean();
        FilterChain chain = (requestToContinue, responseToContinue) -> chainExecuted.set(true);
        request.setAttribute(CsrfToken.class.getName(), token);

        new CsrfCookieFilter().doFilter(request, response, chain);

        assertThat(response.getHeader("X-XSRF-TOKEN")).isEqualTo("csrf-token-value");
        assertThat(chainExecuted).isTrue();
    }
}
