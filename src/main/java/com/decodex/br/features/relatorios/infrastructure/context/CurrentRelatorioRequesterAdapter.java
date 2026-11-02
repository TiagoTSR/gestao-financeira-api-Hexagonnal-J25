package com.decodex.br.features.relatorios.infrastructure.context;

import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.decodex.br.config.multitenancy.TenantContext;
import com.decodex.br.features.relatorios.application.model.RelatorioRequester;
import com.decodex.br.features.relatorios.application.port.out.RelatorioRequesterContextPort;

@Component
public class CurrentRelatorioRequesterAdapter implements RelatorioRequesterContextPort {

    @Override
    public RelatorioRequester currentRequester() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuario = authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())
                        ? authentication.getName()
                        : "SISTEMA";

        return new RelatorioRequester(TenantContext.getTenantId(), usuario);
    }
}
