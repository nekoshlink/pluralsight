package org.nekosoft.shlink.dao.impl

import org.nekosoft.shlink.dao.ShortUrlDataAccess
import org.nekosoft.shlink.entity.*
import org.nekosoft.shlink.entity.support.ShortUrlWithStats
import org.nekosoft.shlink.sec.roles.IsShortUrlAdmin
import org.nekosoft.shlink.sec.roles.IsShortUrlEditor
import org.nekosoft.shlink.sec.roles.IsShortUrlStatsViewer
import org.nekosoft.shlink.sec.roles.IsShortUrlViewer
import org.nekosoft.shlink.vo.ShortUrlListOptions
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
class JpaShortUrlDataAccessImpl(
    private val repo: ShortUrlRepository,
    private val em: EntityManager,
): ShortUrlDataAccess {

    @IsShortUrlEditor
    @Transactional
    override fun create(shortUrl: ShortUrl): ShortUrl {
        return repo.saveAndFlush(shortUrl)
    }

    @IsShortUrlEditor
    @Transactional
    override fun update(shortUrl: ShortUrl): ShortUrl {
        return repo.saveAndFlush(shortUrl)
    }

    @IsShortUrlAdmin
    @Transactional
    override fun delete(shortUrl: ShortUrl) {
        repo.delete(shortUrl)
    }

    @IsShortUrlAdmin
    @Transactional
    override fun deleteById(id: Long) {
        repo.deleteById(id)
    }

    @IsShortUrlViewer
    override fun find(shortCode: String, authority: String): ShortUrl? {
        return repo.findByShortCodeAndDomainAuthority(shortCode, authority)
    }

    @IsShortUrlViewer
    override fun findById(id: Long): ShortUrl? {
        return repo.findById(id).orElse(null)
    }

    @IsShortUrlViewer
    override fun findMatching(meta: ShortUrl): ShortUrl? {
        return repo.findMatching(meta)
    }

    @IsShortUrlViewer
    override fun shortCodeIsUnique(shortUrl: ShortUrl): Boolean {
        return repo.existsByShortCodeAndDomainAuthority(shortUrl.shortCode, shortUrl.domain.authority)
    }

    @IsShortUrlViewer
    override fun findWithDomainFallback(shortCode: String, domain: String): ShortUrl? {
        val results = repo.findEnabledByShortCodeAndDomainWithFallback(shortCode, domain)
        return if (results.isEmpty()) {
            null
        } else {
            results[0]
        }
    }

    @IsShortUrlViewer
    override fun list(pageable: Pageable?): Page<ShortUrl> {
        return repo.findAll(pageable ?: Pageable.unpaged())
    }

    @IsShortUrlStatsViewer
    override fun listWithStats(options: ShortUrlListOptions, pageable: Pageable?): Page<ShortUrlWithStats> {

        // Set up the query string

        val queryStringBuilder = StringBuilder()
        val countStringBuilder = StringBuilder()
        val whereExpressions = mutableListOf<String>()

        if (options.withStats) {
            queryStringBuilder.append("""
                SELECT NEW org.nekosoft.shlink.entity.support.ShortUrlWithStats(
                    s, 
                    COUNT(v.type) AS visitCount,
                    SUM(CASE WHEN v.type = 0 THEN 1 ELSE 0 END) AS successVisitCount, 
                    SUM(CASE WHEN v.type = 2 THEN 1 ELSE 0 END) AS forbiddenVisitCount, 
                    SUM(CASE WHEN v.type = 3 OR v.type = 4 THEN 1 ELSE 0 END) AS outOfTimeVisitCount, 
                    SUM(CASE WHEN v.type = 5 THEN 1 ELSE 0 END) AS overLimitVisitCount, 
                    SUM(CASE WHEN v.type = 1 OR v.type = 6 OR v.type = 7 THEN 1 ELSE 0 END) AS errorVisitCount
                    )
                FROM ShortUrl s
                LEFT JOIN Visit v ON v.shortUrl = s 
            """.trimIndent())
        } else {
            queryStringBuilder.append("""
                SELECT NEW org.nekosoft.shlink.entity.support.ShortUrlWithStats(
                    s, -1L, -1L, -1L, -1L, -1L, -1L
                    ) 
                FROM ShortUrl s 
            """.trimIndent())
        }
        countStringBuilder.append("SELECT COUNT(DISTINCT s.id) FROM ShortUrl s ")

        if (options.term != null) {
            val termJoins = """

                    LEFT JOIN Domain d ON s.domain = d
            """.trimIndent()
            queryStringBuilder.append(termJoins)
            countStringBuilder.append(termJoins)
            whereExpressions.add(searchFields.map { "\n    $it LIKE CONCAT('%', :term, '%')" }.joinToString(" OR ", "( ", " )"))
        }

        if (options.tags != null && options.tags!!.isNotEmpty()) {
            val termJoins = """

                    LEFT JOIN ShortUrlsToTags t ON t.shortUrl = s
            """.trimIndent()
            queryStringBuilder.append(termJoins)
            countStringBuilder.append(termJoins)
            whereExpressions.add("\n    t.tag.name IN :tags")
        }

        if (options.dateFrom != null) {
            whereExpressions.add("\n    s.createdDate >= :dateFrom")
        }

        if (options.dateTo != null) {
            whereExpressions.add("\n    s.createdDate <= :dateTo")
        }

        if (whereExpressions.isNotEmpty()) {
            val whereClauses = whereExpressions.joinToString(" AND ")
            queryStringBuilder.append("""                
                
                WHERE
            """.trimIndent())
            queryStringBuilder.append(whereClauses)
            countStringBuilder.append("\nWHERE ")
            countStringBuilder.append(whereClauses)
        }

        if (options.withStats) {
            queryStringBuilder.append("\nGROUP BY s")
        }

        // Add paging and run the query

        if (pageable?.sort != null && pageable.sort.isSorted) {
            queryStringBuilder.append(
                pageable.sort.map {
                    // make sure the properties for sorting are valid and available (e.g. no stats properties allowed if with-stats is not selected)
                    if (it.property !in possibleSorts.keys || (!options.withStats && it.property in statsSorts)) { throw IllegalArgumentException() } else { possibleSorts[it.property] } + if (it.isAscending) { " ASC" } else { " DESC" }
                }.joinToString(", ", "\nORDER BY ")
            )
        }

        val query = em.createQuery(queryStringBuilder.toString(), ShortUrlWithStats::class.java)
        val count = em.createQuery(countStringBuilder.toString())
        if (options.term != null) {
            query.setParameter("term", options.term)
            count.setParameter("term", options.term)
        }
        if (options.tags != null && options.tags!!.isNotEmpty()) {
            query.setParameter("tags", options.tags)
            count.setParameter("tags", options.tags)
        }
        if (options.dateFrom != null) {
            query.setParameter("dateFrom", options.dateFrom)
            count.setParameter("dateFrom", options.dateFrom)
        }
        if (options.dateTo != null) {
            query.setParameter("dateTo", options.dateTo)
            count.setParameter("dateTo", options.dateTo)
        }

        if (pageable != null && pageable.isPaged) {
            query.firstResult = pageable.pageNumber * pageable.pageSize
            query.maxResults = pageable.pageSize
        }

        val queryResults = query.resultList

        val countResult = count.singleResult as Long

        // Return the paged result

        return PageImpl(queryResults, pageable ?: Pageable.unpaged(), countResult)

    }

    @IsShortUrlViewer
    override fun listCrawlableURLs(): List<String> {
        return repo.findByCrawlableIsTrue()
    }

    companion object {

        val possibleSorts = mapOf(
            "dateCreated" to "s.auditInfo.createdDate",
            "shortCode" to "s.shortCode",
            "title" to "s.title",
            "longUrl" to "s.longUrl",
            "visitCount" to "visitCount",
            "orphanVisitCount" to "orphanVisitCount",
            "successVisitCount" to "successVisitCount",
            "forbiddenVisitCount" to "forbiddenVisitCount",
            "outOfTimeVisitCount" to "outOfTimeVisitCount",
            "overLimitVisitCount" to "overLimitVisitCount",
            "errorVisitCount" to "errorVisitCount",
        )

        val statsSorts = listOf(
            "visitCount",
            "successVisitCount",
            "forbiddenVisitCount",
            "outOfTimeVisitCount",
            "overLimitVisitCount",
            "errorVisitCount",
        )

        val searchFields = listOf(
            "s.shortCode",
            "s.longUrl",
            "s.title",
            "d.authority",
        )
    }

}
