package org.nekosoft.shlink.entity.support

import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import javax.persistence.PostPersist
import javax.persistence.PostRemove
import javax.persistence.PostUpdate

private val kLogger = KotlinLogging.logger {}

class JpaDataAccessAudit {
    @PostPersist
    fun entityAdded(obj: Any) {
        kLogger.warn("USER {} ADDED {}",
            SecurityContextHolder.getContext().authentication?.principal,
            obj
        )
    }

    @PostUpdate
    fun entityModified(obj: Any) {
        kLogger.warn("USER {} MODIFIED {}",
            SecurityContextHolder.getContext().authentication?.principal,
            obj
        )
    }

    @PostRemove
    fun entityDeleted(obj: Any) {
        kLogger.warn("USER {} DELETED {}",
            SecurityContextHolder.getContext().authentication?.principal,
            obj
        )
    }
}