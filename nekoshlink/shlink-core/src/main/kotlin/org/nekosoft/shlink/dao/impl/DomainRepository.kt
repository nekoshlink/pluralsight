package org.nekosoft.shlink.dao.impl

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.nekosoft.shlink.entity.Domain
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@JaversSpringDataAuditable
interface DomainRepository : JpaRepository<Domain, Long> {

    fun findByAuthority(authority: String): Domain?

    fun findByIsDefaultTrue(): Domain

    @Modifying
    @Transactional
    @Query("UPDATE Domain d SET d.isDefault = TRUE WHERE d.authority = ?1")
    fun setDefaultDomain(authority: String)

    @Modifying
    @Transactional
    @Query("UPDATE Domain d SET d.isDefault = FALSE")
    fun prepareToSetDefault()

}
