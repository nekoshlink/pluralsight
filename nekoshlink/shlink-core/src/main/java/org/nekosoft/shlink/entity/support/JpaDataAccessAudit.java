package org.nekosoft.shlink.entity.support;

import kotlin.Unit;
import mu.KLogger;
import mu.KotlinLogging;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public class JpaDataAccessAudit {

    private static KLogger kLogger = KotlinLogging.INSTANCE.logger(() -> Unit.INSTANCE);

    @PostPersist
    public void entityAdded(Object obj) {
        kLogger.warn("USER {} ADDED {}",
            SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null,
            obj
        );
    }

    @PostUpdate
    public void entityModified(Object obj) {
        kLogger.warn("USER {} MODIFIED {}",
                SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null,
            obj
        );
    }

    @PostRemove
    public void entityDeleted(Object obj) {
        kLogger.warn("USER {} DELETED {}",
                SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null,
            obj
        );
    }
}