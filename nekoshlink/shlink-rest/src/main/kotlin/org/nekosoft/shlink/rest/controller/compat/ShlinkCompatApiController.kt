package org.nekosoft.shlink.rest.controller.compat

import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.dao.TagDataAccess
import org.nekosoft.shlink.dao.VisitDataAccess
import org.nekosoft.shlink.entity.Domain
import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.ShortUrl.Companion.URL_SEGMENT_REGEX
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.entity.support.ShortUrlWithStats
import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.entity.support.VisitStats
import org.nekosoft.shlink.entity.support.VisitType
import org.nekosoft.shlink.sec.delegation.annotation.RunAs
import org.nekosoft.shlink.service.ShortUrlManager
import org.nekosoft.shlink.vo.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.ZonedDateTime

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("rest/v2") // this short mapping guarantees it cannot be a short code (min five characters)
class ShlinkCompatApiController(
    private val domains: DomainDataAccess,
    private val shortUrls: ShortUrlManager,
    private val visits: VisitDataAccess,
    private val tags: TagDataAccess,
) {

    //region INTERFACE IMPLEMENTATION

    @PreAuthorize("hasRole('API_Key_User')")
    @GetMapping("health")
    fun health(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.OK).body(mapOf(
            "links" to mapOf(
                "about" to "https://shlink.nekosoft.org",
                "project" to "https://github.com/nekosoft/shlink",
            ),
            "status" to "pass",
            "version" to "3.0.3-nekoshlink",
        ))
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @GetMapping("mercure-info")
    fun mercureInfo(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.OK).body(mapOf<String, Any>(
            "detail" to "This NekoShlink instance is not integrated with a mercure hub.",
            "status" to 501,
            "title" to "Mercure integration not configured",
            "type" to "MERCURE_NOT_CONFIGURED",
        ))
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Domains", "Viewer")
    @GetMapping("domains")
    fun domains(): ResponseEntity<Map<String, Any>> {
        val results = domains.list(null)
        return ResponseEntity.status(HttpStatus.OK).body(mapOf<String, Any>(
            "domains" to mapOf(
                "data" to results.content.map { domainToShlinkData(it) },
                "defaultRedirects" to mapOf(
                    "baseUrlRedirect" to null,
                    "invalidShortUrlRedirect" to null,
                    "regular404Redirect" to null,
                ),
            )
        ))
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Domains", "Editor")
    @PatchMapping("domains/redirects")
    fun domainRedirects(@RequestBody redirects: DomainRedirectsRequest): ResponseEntity<Map<String, Any?>> {
        val domain = domains.findByAuthority(redirects.domain)
            ?: return ResponseEntity.notFound().build()
        domain.baseUrlRedirect = redirects.baseUrlRedirect
        domain.regular404Redirect = redirects.regular404Redirect
        domain.invalidShortUrlRedirect = redirects.invalidShortUrlRedirect
        domains.update(domain)
        return ResponseEntity.status(HttpStatus.OK).body(mapOf<String, Any?>(
            "baseUrlRedirect" to domain.baseUrlRedirect,
            "invalidShortUrlRedirect" to domain.regular404Redirect,
            "regular404Redirect" to domain.invalidShortUrlRedirect,
        ))
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Tags", "Editor")
    @PutMapping("tags")
    fun renameTag(@RequestBody meta: TagRenameMeta): ResponseEntity<Void> {
        tags.rename(meta.oldName, meta.newName)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Tags", "Admin")
    @DeleteMapping("tags")
    fun deleteTags(@RequestParam("tags[]") names: List<String>): ResponseEntity<Void> {
        names.forEach {
            tags.deleteByName(it)
        }
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("ShortUrls", "Viewer", "Stats")
    @GetMapping("short-urls")
    fun shortUrls(
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("itemsPerPage", required = false) itemsPerPage: Int?,
        @RequestParam("orderBy", required = false) orderBy: String?,
        @RequestParam("searchTerm", required = false) search: String?,
        @RequestParam("startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: ZonedDateTime?,
        @RequestParam("endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: ZonedDateTime?,
        @RequestParam("tags[]", required = false) tags: List<String>?,
    ): ResponseEntity<Map<String, Any>> {
        val sort = if (orderBy != null) {
            val (property, direction) = orderBy.split('-')
            Sort.by(
                try { Sort.Direction.valueOf(direction) } catch (e: Exception) { Sort.Direction.ASC },
                if (property == "visits") { "visitCount" } else { property }
            )
        } else {
            Sort.by(Sort.Direction.ASC, "dateCreated")
        }
        val results = shortUrls.listWithStats(
            ShortUrlListOptions(
                withStats = true,
                term = search,
                dateFrom = startDate?.toLocalDateTime(),
                dateTo = endDate?.toLocalDateTime(),
                tags = tags
            ),
            PageRequest.of(
                if (page != null && page > 0) { page - 1 } else { 0 },
                itemsPerPage ?: 9,
                sort
            )
        )
        return ResponseEntity.status(HttpStatus.OK).body(mapOf<String, Any>(
            "shortUrls" to mapOf(
                "data" to results.content.map { shortUrlWithStatsToShlinkData(it) },
                "pagination" to mapOf(
                    "currentPage" to results.number + 1,
                    "itemsPerPage" to results.size,
                    "itemsInCurrentPage" to results.numberOfElements,
                    "pagesCount" to results.totalPages,
                    "totalItems" to results.totalElements,
                ),
            )
        ))
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("ShortUrls", "Viewer")
    @GetMapping("short-urls/{shortCode:$URL_SEGMENT_REGEX}")
    fun shortUrl(@PathVariable("shortCode") shortCode: String, @RequestParam("domain", required = false) domain: String?): ResponseEntity<Map<String,Any?>> {
        val shortUrl = shortUrls.retrieve(
            ShortUrlRetrieveOptions(
                shortCode = shortCode,
                domain = domain,
            )
        )
        return ResponseEntity.ok(shortUrlToShlinkData(shortUrl))
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Visits", "Viewer")
    @GetMapping("short-urls/{shortCode:$URL_SEGMENT_REGEX}/visits")
    fun getVisitsForShortUrl(
        @PathVariable("shortCode") shortCode: String,
        @RequestParam("domain") domain: String,
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("itemsPerPage", required = false) itemsPerPage: Int?,
        @RequestParam("orderBy", required = false) orderBy: String?,
        @RequestParam("startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: ZonedDateTime?,
        @RequestParam("endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: ZonedDateTime?,
    ): ResponseEntity<Map<String,Any?>> {

        return getVisitList(
            shortCode = shortCode,
            domain = domain,
            page = page,
            itemsPerPage = itemsPerPage,
            orderBy = orderBy,
            startDate = startDate,
            endDate = endDate,
        )

    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("ShortUrls", "Editor")
    @PostMapping("short-urls")
    fun createShortUrl(@RequestBody shortUrlCreation: ShortUrlCreateRequest): ResponseEntity<Map<String,Any?>> {
        shortUrlCreation.meta?.validFrom = shortUrlCreation.validSince
        shortUrlCreation.meta?.validUntil = shortUrlCreation.validUntil
        val shortUrl = shortUrls.create(
            shortUrlCreation.meta ?: return ResponseEntity.badRequest().body(null),
            shortUrlCreation.options ?: return ResponseEntity.badRequest().body(null),
        )
        return ResponseEntity.status(HttpStatus.OK).body(shortUrlToShlinkData(shortUrl))
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("ShortUrls", "Editor")
    @PatchMapping("short-urls/{shortCode:$URL_SEGMENT_REGEX}")
    fun editShortUrl(@PathVariable("shortCode") shortCode: String, @RequestBody shortUrlUpdate: ShortUrlUpdateRequest): ResponseEntity<ShortUrl> {
        shortUrlUpdate.options?.shortCode = shortCode
        shortUrlUpdate.meta?.validFrom = shortUrlUpdate.validSince
        shortUrlUpdate.meta?.validUntil = shortUrlUpdate.validUntil
        val shortUrl = shortUrls.edit(
                shortUrlUpdate.meta ?: return ResponseEntity.badRequest().body(null),
                shortUrlUpdate.options ?: return ResponseEntity.badRequest().body(null),
            )
        return ResponseEntity.status(HttpStatus.OK).body(shortUrl)
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("ShortUrls", "Admin")
    @DeleteMapping("short-urls/{shortCode:$URL_SEGMENT_REGEX}")
    fun deleteShortUrl(@PathVariable("shortCode") shortCode: String, @RequestParam("domain") domain: String): ResponseEntity<Void> {
        shortUrls.delete(
            ShortUrlEditOptions(
                shortCode = shortCode,
                domain = domain,
            )
        )
        /*
         * AS PER RFC 7231 (https://www.rfc-editor.org/rfc/rfc7231)
         * If a DELETE method is successfully applied, the origin server SHOULD send a 202 (Accepted) status code
         * if the action will likely succeed but has not yet been enacted, a 204 (No Content) status code if the action
         * has been enacted and no further information is to be supplied, or a 200 (OK) status code if the action has
         * been enacted and the response message includes a representation describing the status.
         */
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Visits", "Viewer")
    @GetMapping("visits")
    fun visits(): ResponseEntity<Map<String, Any>> {
        val stats = visits.visitStats()
        return ResponseEntity.status(HttpStatus.OK).body(mapOf<String, Any>(
            "visits" to visitStatsToShlinkData(stats)
        ))
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Visits", "Viewer")
    @GetMapping("visits/non-orphan")
    fun nonOrphanVisits(
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("itemsPerPage", required = false) itemsPerPage: Int?,
        @RequestParam("orderBy", required = false) orderBy: String?,
        @RequestParam("startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: ZonedDateTime?,
        @RequestParam("endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: ZonedDateTime?,
    ): ResponseEntity<Map<String, Any?>> {

        return getVisitList(
            types = listOf(VisitType.SUCCESSFUL, VisitType.INVALID_REQUEST, VisitType.FORBIDDEN, VisitType.EXPIRED, VisitType.DISABLED, VisitType.OVER_LIMIT, VisitType.PATH_TRAIL_ERROR, VisitType.QUERY_PARAMS_ERROR),
            page = page,
            itemsPerPage = itemsPerPage,
            orderBy = orderBy,
            startDate = startDate,
            endDate = endDate,
        )

    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Visits", "Viewer")
    @GetMapping("visits/orphan")
    fun orphanVisits(
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("itemsPerPage", required = false) itemsPerPage: Int?,
        @RequestParam("orderBy", required = false) orderBy: String?,
        @RequestParam("startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: ZonedDateTime?,
        @RequestParam("endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: ZonedDateTime?,
    ): ResponseEntity<Map<String, Any?>> {

        return getVisitList(
            types = listOf(VisitType.ORPHAN, VisitType.INVALID_REQUEST),
            page = page,
            itemsPerPage = itemsPerPage,
            orderBy = orderBy,
            startDate = startDate,
            endDate = endDate,

        )

    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Visits", "Viewer")
    @GetMapping("tags/{tag}/visits")
    fun visitsPerTag(
        @PathVariable("tag") name: String,
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("itemsPerPage", required = false) itemsPerPage: Int?,
        @RequestParam("orderBy", required = false) orderBy: String?,
        @RequestParam("startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: ZonedDateTime?,
        @RequestParam("endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: ZonedDateTime?,
    ): ResponseEntity<Map<String, Any?>> {

        return getVisitList(
            tagNames = listOf(name),
            page = page,
            itemsPerPage = itemsPerPage,
            orderBy = orderBy,
            startDate = startDate,
            endDate = endDate,
        )

    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Tags", "Viewer", "Stats")
    @GetMapping("tags")
    fun tags(
        @RequestParam("withStats", required = false) withStats: Boolean?,
        @RequestParam("searchTerm", required = false) term: String?,
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("itemsPerPage", required = false) itemsPerPage: Int?,
    ): ResponseEntity<Map<String, Any?>> {
        val results = tags.findAll(
            TagListOptions(namePattern = term, withStats = withStats ?: false),
            PageRequest.of(
                if (page != null && page > 0) { page - 1 } else { 0 },
                itemsPerPage ?: 300,
            )
        )
        return ResponseEntity.status(HttpStatus.OK).body(mapOf(
            "tags" to mapOf(
                "data" to results.content.map { it.name },
                "pagination" to mapOf(
                    "currentPage" to results.number + 1,
                    "itemsPerPage" to results.size,
                    "itemsInCurrentPage" to results.numberOfElements,
                    "pagesCount" to results.totalPages,
                    "totalItems" to results.totalElements,
                ),
                "stats" to results.content.map { mapOf(
                    "shortUrlsCount" to it.shortUrlCount,
                    "visitsCount" to it.visitCount,
                    "tag" to it.name,
                ) },
            )
        ))
    }

    @PreAuthorize("hasRole('API_Key_User')")
    @RunAs("Tags", "Viewer", "Stats")
    @GetMapping("tags/stats")
    fun tagStats(
        @RequestParam("searchTerm", required = false) term: String?,
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("itemsPerPage", required = false) itemsPerPage: Int?,
    ): ResponseEntity<Map<String, Any?>> = tags(true, term, page, itemsPerPage)

    //endregion

    //region INTERFACE IMPLEMENTATION SUPPORT

    private fun getVisitList(
        shortCode: String? = null,
        domain: String? = null,
        tagNames: List<String>? = null,
        types: List<VisitType>? = null,
        sources: List<VisitSource>? = null,
        startDate: ZonedDateTime? = null,
        endDate: ZonedDateTime? = null,
        page: Int? = null,
        itemsPerPage: Int? = null,
        orderBy: String? = null,
    ): ResponseEntity<Map<String, Any?>> {

        val sort = if (orderBy != null) {
            val (property, direction) = orderBy.split('-')
            Sort.by(
                try { Sort.Direction.valueOf(direction) } catch (e: Exception) { Sort.Direction.ASC },
                property
            )
        } else {
            Sort.by(Sort.Direction.ASC, "dateCreated")
        }
        val results = visits.getVisits(
            VisitListOptions(
                shortCode = shortCode,
                domain = domain,
                tags = tagNames,
                sources = sources,
                types = types,
                dateFrom = startDate?.toLocalDateTime(),
                dateTo = endDate?.toLocalDateTime(),
            ),
            PageRequest.of(
                if (page != null && page > 0) { page - 1 } else { 0 },
                itemsPerPage ?: 100,
                sort
            )
        )
        return ResponseEntity.status(HttpStatus.OK).body(mapOf<String, Any>(
            "visits" to mapOf(
                "data" to results.content.map { visitToShlinkData(it) },
                "pagination" to mapOf(
                    "currentPage" to results.number + 1,
                    "itemsPerPage" to results.size,
                    "itemsInCurrentPage" to results.numberOfElements,
                    "pagesCount" to results.totalPages,
                    "totalItems" to results.totalElements,
                ),
            )
        ))
    }

    private fun shortUrlToShlinkData(shortUrl: ShortUrl): Map<String, Any?> {
        return mapOf<String, Any?>(
            "authorApiKey" to shortUrl.authorApiKey,
            "crawlable" to shortUrl.crawlable,
            "customSlugWasProvided" to shortUrl.customSlugWasProvided,
            "dateCreated" to shortUrl.auditInfo.createdDate,
            "domain" to shortUrl.domain.authority,
            "forwardPathTrail" to shortUrl.forwardPathTrail,
            "forwardQuery" to shortUrl.forwardQuery,
            "id" to shortUrl.id,
            "importOriginalShortCode" to shortUrl.importOriginalShortCode,
            "importSource" to shortUrl.importSource,
            "longUrl" to shortUrl.longUrl,
            "password" to shortUrl.password,
            "qrShortUrl" to shortUrl.qrShortUrl,
            "shortUrl" to shortUrl.shortUrl,
            "shortCode" to shortUrl.shortCode,
            "tags" to shortUrl.tags.map { it.tag.name },
            "title" to shortUrl.title,
            "titleWasAutoResolved" to shortUrl.titleWasAutoResolved,
            "visitsCount" to 0,
            "meta" to mapOf<String, Any?>(
                "validSince" to shortUrl.validFrom,
                "validUntil" to shortUrl.validUntil,
                "maxVisits" to shortUrl.maxVisits
            )
        )
    }

    private fun shortUrlWithStatsToShlinkData(shortUrlWithStats: ShortUrlWithStats): Map<String, Any?> {
        val shortUrl = shortUrlWithStats.shortUrl
        return mapOf<String, Any?>(
            "authorApiKey" to shortUrl.authorApiKey,
            "crawlable" to shortUrl.crawlable,
            "customSlugWasProvided" to shortUrl.customSlugWasProvided,
            "dateCreated" to shortUrl.auditInfo.createdDate,
            "domain" to shortUrl.domain.authority,
            "forwardPathTrail" to shortUrl.forwardPathTrail,
            "forwardQuery" to shortUrl.forwardQuery,
            "id" to shortUrl.id,
            "importOriginalShortCode" to shortUrl.importOriginalShortCode,
            "importSource" to shortUrl.importSource,
            "longUrl" to shortUrl.longUrl,
            "password" to shortUrl.password,
            "qrShortUrl" to shortUrl.qrShortUrl,
            "shortUrl" to shortUrl.shortUrl,
            "shortCode" to shortUrl.shortCode,
            "tags" to shortUrl.tags.map { it.tag.name },
            "title" to shortUrl.title,
            "titleWasAutoResolved" to shortUrl.titleWasAutoResolved,
            "visitsCount" to shortUrlWithStats.visitCount,
            "meta" to mapOf<String, Any?>(
                "validSince" to shortUrl.validFrom,
                "validUntil" to shortUrl.validUntil,
                "maxVisits" to shortUrl.maxVisits
            )
        )
    }

    private fun domainToShlinkData(domain: Domain): Map<String, Any?> {
        return mapOf(
            "scheme" to domain.scheme,
            "domain" to domain.authority,
            "isDefault" to domain.isDefault,
            "redirects" to mapOf(
                "baseUrlRedirect" to domain.baseUrlRedirect,
                "regular404Redirect" to domain.regular404Redirect,
                "invalidShortUrlRedirect" to domain.invalidShortUrlRedirect,
            ),
        )
    }

    private fun visitStatsToShlinkData(stats: VisitStats): Map<String, Any?> {
        return mapOf(
            "visitsCount" to stats.totalCount,
            "orphanVisitsCount" to stats.orphanCount,
        )
    }

    private fun visitToShlinkData(visit: Visit): Map<String, Any?> {
        return mapOf(
            "referer" to visit.referrer,
            "date" to visit.date,
            "userAgent" to visit.userAgent,
            "visitLocation" to mapOf(
                "countryCode" to null,
                "countryName" to null,
                "regionName" to null,
                "cityName" to null,
                "latitude" to null,
                "longitude" to null,
                "timezone" to null,
                "isEmpty" to true,
            ),
            "potentialBot" to false,
            "visitedUrl" to if (visit.type == VisitType.ORPHAN) {
                visit.domain + "/" + visit.shortCode
            } else {
                visit.visitedUrl
            },
            "type" to if (visit.type == VisitType.ORPHAN) { "base_url" } else { null },
        )
    }

    //endregion

}
