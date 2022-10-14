package org.nekosoft.shlink.dao

import org.nekosoft.shlink.entity.Domain
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface DomainDataAccess {
    fun create(domain: Domain): Domain
    fun update(domain: Domain): Domain
    fun remove(authority: String)
    fun list(pageable: Pageable? = null): Page<Domain>
    fun findByAuthority(authority: String?): Domain?
    fun getDefault(): Domain
    fun getDefaultAuthority(): String
    fun makeDefault(authority: String): Domain
    fun resolveDefaultAuthority(authority: String?): String
}
