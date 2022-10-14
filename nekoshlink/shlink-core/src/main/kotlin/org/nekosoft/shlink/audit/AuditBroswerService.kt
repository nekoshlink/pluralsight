package org.nekosoft.shlink.audit

interface AuditBrowserService {
    fun <T> getChanges(entity: Class<T>, id: Long): List<T>
}