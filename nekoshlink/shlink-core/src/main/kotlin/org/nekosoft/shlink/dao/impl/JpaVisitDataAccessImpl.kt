package org.nekosoft.shlink.dao.impl

import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.dao.VisitDataAccess
import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.entity.support.ShortUrlWithStats
import org.nekosoft.shlink.entity.support.VisitStats
import org.nekosoft.shlink.sec.roles.IsVisitEditor
import org.nekosoft.shlink.sec.roles.IsVisitStatsViewer
import org.nekosoft.shlink.sec.roles.IsVisitViewer
import org.nekosoft.shlink.vo.VisitListOptions
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
class JpaVisitDataAccessImpl(
    private val repo: VisitRepository,
    private val domains: DomainDataAccess,
    private val em: EntityManager,
): VisitDataAccess {

    @IsVisitEditor
    override fun add(visit: Visit) {
        repo.saveAndFlush(visit)
    }

    @IsVisitStatsViewer
    override fun visitStats(): VisitStats {
        return repo.visitStats()
    }

    @IsVisitViewer
    override fun visitStatsPerShortUrl(shortUrl: ShortUrl): ShortUrlWithStats {
        return repo.visitStatsPerShortUrl(shortUrl)
    }

    @IsVisitEditor
    override fun removeShortUrlReference(shortUrlId: Long) {
        repo.setShortUrlToNull(shortUrlId)
    }

    @IsVisitViewer
    override fun getVisits(options: VisitListOptions, pageable: Pageable?): Page<Visit> {

        // Set up the query string

        val queryStringBuilder = StringBuilder()
        val countStringBuilder = StringBuilder()
        val whereExpressions = mutableListOf<String>()

        queryStringBuilder.append("SELECT v FROM Visit v ")
        countStringBuilder.append("SELECT COUNT(DISTINCT v.id) FROM Visit v ")

        if (options.shortCode != null) {
            val joinClause = """

                    LEFT JOIN ShortUrl s ON v.shortUrl = s
                    LEFT JOIN s.domain d
                """.trimIndent()
            queryStringBuilder.append(joinClause)
            countStringBuilder.append(joinClause)
            whereExpressions.add("\n    (s.shortCode = :shortCode AND d.authority = :domain)")
        }

        if (options.tags != null && options.tags!!.isNotEmpty()) {
            val tagsJoins = if (options.shortCode == null) {
                """

                    LEFT JOIN ShortUrl s ON v.shortUrl = s
                    LEFT JOIN s.tags st
                    LEFT JOIN Tag t ON st.tag = t
                """.trimIndent()
            } else {
                """

                    LEFT JOIN s.tags st
                    LEFT JOIN Tag t ON st.tag = t
                """.trimIndent()
            }
            queryStringBuilder.append(tagsJoins)
            countStringBuilder.append(tagsJoins)
            whereExpressions.add("\n    t.name IN :tags")
        }

        if (options.types != null && options.types!!.isNotEmpty()) {
            whereExpressions.add("\n    v.type IN :types")
        }

        if (options.sources != null && options.sources!!.isNotEmpty()) {
            whereExpressions.add("\n    v.source IN :sources")
        }

        if (options.dateFrom != null) {
            whereExpressions.add("\n    v.date >= :dateFrom")
        }

        if (options.dateTo != null) {
            whereExpressions.add("\n    v.date <= :dateTo")
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

        // Add paging and run the query

        if (pageable?.sort != null && pageable.sort.isSorted) {
            queryStringBuilder.append(
                // make sure the properties for sorting are valid
                pageable.sort.map {
                    if (it.property !in possibleSorts.keys) { throw IllegalArgumentException() } else { possibleSorts[it.property] } + if (it.isAscending) { " ASC" } else { " DESC" }
                }.joinToString(", ", "\nORDER BY ")
            )
        }

        val query = em.createQuery(queryStringBuilder.toString(), Visit::class.java)
        val count = em.createQuery(countStringBuilder.toString())
        if (options.shortCode != null) {
            val domain = options.domain ?: domains.getDefaultAuthority()
            query.setParameter("domain", domain)
            count.setParameter("domain", domain)
            query.setParameter("shortCode", options.shortCode)
            count.setParameter("shortCode", options.shortCode)
        }
        if (options.tags != null && options.tags!!.isNotEmpty()) {
            query.setParameter("tags", options.tags)
            count.setParameter("tags", options.tags)
        }
        if (options.sources != null && options.sources!!.isNotEmpty()) {
            query.setParameter("sources", options.sources)
            count.setParameter("sources", options.sources)
        }
        if (options.types != null && options.types!!.isNotEmpty()) {
            query.setParameter("types", options.types)
            count.setParameter("types", options.types)
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

    companion object {

        val possibleSorts = mapOf(
            "dateCreated" to "v.date",
        )

    }

}
