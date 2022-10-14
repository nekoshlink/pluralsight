package org.nekosoft.shlink.audit.impl

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.repository.jql.QueryBuilder
import org.nekosoft.shlink.audit.AuditBrowserService
import org.springframework.stereotype.Service

@Service
class JaversAuditBroswerServiceImpl(val javers: Javers) : AuditBrowserService {
    override fun <T> getChanges(entity: Class<T>, id: Long): List<T> {
        val query = QueryBuilder.byInstanceId(id, entity).build()
        val shadows = javers.findShadows<T>(query)
        return shadows.map { it.get() }
    }
}