package com.decodex.br.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

import com.decodex.br.config.multitenancy.TenantContext;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final JpaUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(@Lazy TokenService tokenService, @Lazy JpaUserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            var token = this.recoverToken(request);
            if (token != null) {
                var username = tokenService.validateToken(token);
                if (username != null) {
                    UUID tenantId = tokenService.extractTenantId(token);
                    TenantContext.setTenantId(tenantId);

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails user = userDetailsService.loadUserByUsername(username);
                        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } else {
                String xTenant = request.getHeader("X-Tenant-ID");
                if (xTenant != null && !xTenant.isBlank()) {
                    try {
                        TenantContext.setTenantId(UUID.fromString(xTenant));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        var cookie = org.springframework.web.util.WebUtils.getCookie(request, "token");
        if (cookie != null) {
            return cookie.getValue();
        }
        
        return null;
    }
}
