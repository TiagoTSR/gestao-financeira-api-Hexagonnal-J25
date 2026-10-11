package com.decodex.br.config.audit;

import com.decodex.br.config.multitenancy.TenantContext;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        if (revisionEntity instanceof AuditRevisionEntity audit) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                audit.setUsuario(auth.getName());
            } else {
                audit.setUsuario("SISTEMA");
            }
            audit.setTenantId(TenantContext.getTenantId());
        }
    }
}
