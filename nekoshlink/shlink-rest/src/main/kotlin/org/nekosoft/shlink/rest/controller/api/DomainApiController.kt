package org.nekosoft.shlink.rest.controller.api

import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.entity.Domain
import org.nekosoft.shlink.rest.ShlinkRestApiServer.Companion.VERSION_STRING
import org.nekosoft.shlink.vo.rest.PaginationData
import org.nekosoft.shlink.vo.rest.PaginationData.Companion.paginationToPageable
import org.nekosoft.shlink.vo.rest.RestResult
import org.nekosoft.shlink.vo.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("api/v$VERSION_STRING/domains") // this short mapping guarantees it cannot be a short code (min five characters)
class DomainApiController(
    private val domains: DomainDataAccess,
) {

    @PreAuthorize("hasRole('ROLE_Viewer') and hasRole('ROLE_Domains')")
    @GetMapping
    fun domains(options: DomainListOptions, pagination: PaginationOptions): ResponseEntity<RestResult<Domain>> {
        val results = domains.list(paginationToPageable(pagination))
        return ResponseEntity.status(HttpStatus.OK).body(RestResult(
            results = results.content,
            pagination = PaginationData.fromPage(results)
        ))
    }

    @PreAuthorize("hasRole('ROLE_Admin') and hasRole('ROLE_Domains')")
    @PostMapping
    fun createDomain(@RequestBody meta: DomainCreateMeta): ResponseEntity<Domain> {
        val domain = domains.create(Domain(
            authority = meta.authority,
            scheme = meta.scheme,
            baseUrlRedirect = meta.baseUrlRedirect,
            regular404Redirect = meta.regular404Redirect,
            invalidShortUrlRedirect = meta.invalidShortUrlRedirect,
            isDefault = false,
        ))
        return ResponseEntity.status(HttpStatus.OK).body(domain)
    }

    @PreAuthorize("hasRole('ROLE_Admin') and hasRole('ROLE_Domains')")
    @PatchMapping("default")
    fun makeDefault(@RequestBody meta: DomainDefaultMeta): ResponseEntity<Void> {
        domains.makeDefault(meta.authority)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @PreAuthorize("hasRole('ROLE_Editor') and hasRole('ROLE_Domains')")
    @PutMapping("{authority}")
    fun editDomain(@PathVariable("authority") authority: String, @RequestBody meta: DomainEditMeta): ResponseEntity<Void> {
        val domain = Domain(
            authority = authority,
            scheme = meta.scheme,
            baseUrlRedirect = meta.baseUrlRedirect,
            regular404Redirect = meta.regular404Redirect,
            invalidShortUrlRedirect = meta.invalidShortUrlRedirect,
        )
        domains.update(domain)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @PreAuthorize("hasRole('ROLE_Admin') and hasRole('ROLE_Domains')")
    @DeleteMapping("{authority}")
    fun editDomain(@PathVariable("authority") authority: String): ResponseEntity<Void> {
        domains.remove(authority)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

}
