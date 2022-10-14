package org.nekosoft.shlink.dao

import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.entity.support.ShortUrlWithStats
import org.nekosoft.shlink.entity.support.VisitStats
import org.nekosoft.shlink.vo.VisitListOptions
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface VisitDataAccess {
    fun add(visit: Visit)
    fun visitStats(): VisitStats
    fun visitStatsPerShortUrl(shortUrl: ShortUrl): ShortUrlWithStats
    fun getVisits(options: VisitListOptions, pageable: Pageable? = null): Page<Visit>
    fun removeShortUrlReference(shortUrlId: Long)
}