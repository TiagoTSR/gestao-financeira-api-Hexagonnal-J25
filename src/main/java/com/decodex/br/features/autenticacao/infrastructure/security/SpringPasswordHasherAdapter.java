package com.decodex.br.features.autenticacao.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.decodex.br.features.autenticacao.application.port.out.PasswordHasher;

@Component
public class SpringPasswordHasherAdapter implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public SpringPasswordHasherAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
