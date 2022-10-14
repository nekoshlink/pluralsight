package org.nekosoft.shlink.audit.impl

import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.query.AuditEntity
import org.nekosoft.shlink.audit.AuditBrowserService
import org.springframework.stereotype.Service
import javax.persistence.EntityManager


@Service
class EnversAuditBroswerServiceImpl(val em: EntityManager) : AuditBrowserService {
    override fun <T> getChanges(entity: Class<T>, id: Long): List<T> {
        val results = AuditReaderFactory
            .get(em)
            .createQuery()
            .forRevisionsOfEntity(entity, true, true)
            .add(AuditEntity.id().eq(id))
            .resultList.toList()
        return results.map { it as T }
    }
}