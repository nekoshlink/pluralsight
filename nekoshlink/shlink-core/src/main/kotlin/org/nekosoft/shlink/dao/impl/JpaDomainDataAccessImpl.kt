package org.nekosoft.shlink.dao.impl

import mu.KotlinLogging
import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.entity.*
import org.nekosoft.shlink.entity.Domain.Companion.DEFAULT_DOMAIN
import org.nekosoft.shlink.service.exception.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URL
import javax.annotation.PostConstruct
import javax.transaction.Transactional

private val kLogger = KotlinLogging.logger {  }

@Service
class JpaDomainDataAccessImpl(private val repo: DomainRepository): DomainDataAccess {

    @Value("\${nekoshlink.initial-default-domain}")
    private lateinit var _defaultDomain: String

    private var _defaultAuthority: String = ""

    @PostConstruct
    fun init() {
        val defaultUrl = try {
            URL(_defaultDomain)
        } catch (_: MalformedURLException) {
            throw InvalidDefaultDomainException(_defaultDomain)
        }
        val currentDefaultDomain = try {
            // return the current default domain, if there is one
            getDefault()
        } catch (_: DefaultDomainDoesNotExistsException) {
            null
        }
        val defaultDomain = if (currentDefaultDomain == null) {
            // if there is no current default domain
            findByAuthority(defaultUrl.authority) ?: create(Domain(scheme = defaultUrl.protocol, authority = defaultUrl.authority))
            makeDefault(defaultUrl.authority)
        } else {
            if (currentDefaultDomain.authority != defaultUrl.authority) {
                kLogger.warn("Your configured default domain [${defaultUrl.authority}] was not set because [${currentDefaultDomain.authority}] is already set as the default")
            }
            currentDefaultDomain
        }
        _defaultAuthority = defaultDomain.authority
    }

    @PreAuthorize("permitAll()")
    override fun getDefaultAuthority(): String {
        return _defaultAuthority
    }

    @PreAuthorize("hasRole('Admin') and hasRole('Domains')")
    @Transactional
    override fun create(domain: Domain): Domain {
        if (domain.authority == DEFAULT_DOMAIN || domain.isDefault) {
            throw DefaultDomainAlreadyExistsException(domain.authority)
        }
        if (repo.findByAuthority(domain.authority) != null) {
            throw DomainAlreadyExistsException(domain.authority)
        }
        return repo.save(domain)
    }

    @PreAuthorize("hasRole('Editor') and hasRole('Domains')")
    @Transactional
    override fun update(domain: Domain): Domain {
        val authority = resolveDefaultAuthority(domain.authority)
        val updateDomain = repo.findByAuthority(authority) ?: throw DomainDoesNotExistException(authority)
        updateDomain.scheme = domain.scheme
        updateDomain.baseUrlRedirect = domain.baseUrlRedirect
        updateDomain.regular404Redirect = domain.regular404Redirect
        updateDomain.invalidShortUrlRedirect = domain.invalidShortUrlRedirect
        return repo.saveAndFlush(updateDomain)
    }

    @PreAuthorize("hasRole('Admin') and hasRole('Domains')")
    @Transactional
    override fun remove(authority: String) {
        val resolvedAuthority = resolveDefaultAuthority(authority)
        val removeDomain = repo.findByAuthority(resolvedAuthority) ?: throw DomainDoesNotExistException(resolvedAuthority)
        repo.delete(removeDomain)
    }

    @PreAuthorize("hasRole('Viewer') and hasRole('Domains')")
    override fun list(pageable: Pageable?): Page<Domain> {
        return repo.findAll(pageable ?: Pageable.unpaged())
    }

    @PreAuthorize("hasRole('Viewer') and hasRole('Domains')")
    override fun findByAuthority(authority: String?): Domain? {
        if (authority == null || authority == DEFAULT_DOMAIN) {
            return getDefault()
        }
        return repo.findByAuthority(authority)
    }

    @PreAuthorize("hasRole('Viewer') and hasRole('Domains')")
    override fun getDefault(): Domain {
        try {
            return repo.findByIsDefaultTrue()
        } catch (_: EmptyResultDataAccessException) {
            throw DefaultDomainDoesNotExistsException()
        }
    }

    @PreAuthorize("hasRole('Admin') and hasRole('Domains')")
    @Transactional
    override fun makeDefault(authority: String): Domain {
        val resolvedAuthority = resolveDefaultAuthority(authority)
        try {
            val currentDefault = repo.findByIsDefaultTrue()
            if (currentDefault.authority == resolvedAuthority) {
                return currentDefault
            }
        } catch (_: EmptyResultDataAccessException) {
            // proceed as normal if there is not a default domain yet
        }
        repo.findByAuthority(resolvedAuthority) ?: throw DomainDoesNotExistException(resolvedAuthority)
        repo.prepareToSetDefault()
        repo.setDefaultDomain(resolvedAuthority)
        _defaultAuthority = resolvedAuthority
        return getDefault()
    }

    @PreAuthorize("permitAll()")
    override fun resolveDefaultAuthority(authority: String?): String =
        if (authority == null || authority == DEFAULT_DOMAIN) {
            getDefaultAuthority()
        } else {
            authority
        }

}
