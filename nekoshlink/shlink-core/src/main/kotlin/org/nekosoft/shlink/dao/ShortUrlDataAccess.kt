package org.nekosoft.shlink.dao

import org.nekosoft.shlink.entity.Domain.Companion.DEFAULT_DOMAIN
import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.support.ShortUrlWithStats
import org.nekosoft.shlink.vo.ShortUrlListOptions
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ShortUrlDataAccess {
    fun create(shortUrl: ShortUrl): ShortUrl
    fun update(shortUrl: ShortUrl): ShortUrl
    fun delete(shortUrl: ShortUrl)
    fun deleteById(id: Long)
    fun find(shortCode: String, authority: String = DEFAULT_DOMAIN): ShortUrl?
    fun findById(id: Long): ShortUrl?
    fun findMatching(meta: ShortUrl): ShortUrl?
    fun shortCodeIsUnique(shortUrl: ShortUrl): Boolean
    fun findWithDomainFallback(shortCode: String, domain: String): ShortUrl?
    fun list(pageable: Pageable? = null): Page<ShortUrl>
    fun listWithStats(options: ShortUrlListOptions, pageable: Pageable? = null): Page<ShortUrlWithStats>
    fun listCrawlableURLs(): List<String>
}
