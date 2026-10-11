package com.decodex.br.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.decodex.br.config.multitenancy.TenantContext;
import java.util.UUID;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UserDetails userDetails, UUID tenantId) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            UUID tenant = tenantId != null ? tenantId : TenantContext.getTenantId();
            return JWT.create()
                    .withIssuer("gestao-financeira-api")
                    .withSubject(userDetails.getUsername())
                    .withClaim("tenant_id", tenant.toString())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, TenantContext.getTenantId());
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("gestao-financeira-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public UUID extractTenantId(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String tenantStr = JWT.require(algorithm)
                    .withIssuer("gestao-financeira-api")
                    .build()
                    .verify(token)
                    .getClaim("tenant_id")
                    .asString();
            return tenantStr != null ? UUID.fromString(tenantStr) : TenantContext.DEFAULT_TENANT_ID;
        } catch (Exception exception) {
            return TenantContext.DEFAULT_TENANT_ID;
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.of("-03:00"));
    }
}
