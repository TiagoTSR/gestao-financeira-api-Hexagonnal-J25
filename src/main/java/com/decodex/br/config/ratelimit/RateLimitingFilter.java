package com.decodex.br.config.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    private final int capacity;
    private final int durationInMinutes;
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public RateLimitingFilter(
            @Value("${api.rate-limit.capacity}") int capacity,
            @Value("${api.rate-limit.duration-in-minutes}") int durationInMinutes
    ) {
        this.capacity = capacity;
        this.durationInMinutes = durationInMinutes;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (!(request instanceof HttpServletRequest httpServletRequest) || 
            !(response instanceof HttpServletResponse httpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(httpServletRequest);
        Bucket bucket = cache.computeIfAbsent(ip, this::createNewBucket);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        httpServletResponse.addHeader("X-Rate-Limit-Limit", String.valueOf(capacity));
        httpServletResponse.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));

        if (probe.isConsumed()) {
            chain.doFilter(request, response);
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            if (waitForRefill == 0) {
                waitForRefill = 1; // Garante pelo menos 1 segundo no Retry-After
            }
            httpServletResponse.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            httpServletResponse.setStatus(429); // Too Many Requests
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            
            String jsonError = String.format(
                "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Limite de requisições excedido. Tente novamente em %d segundos.\",\"path\":\"%s\"}",
                waitForRefill,
                httpServletRequest.getRequestURI()
            );
            httpServletResponse.getWriter().write(jsonError);
        }
    }

    private Bucket createNewBucket(String ip) {
        Refill refill = Refill.greedy(capacity, Duration.ofMinutes(durationInMinutes));
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
