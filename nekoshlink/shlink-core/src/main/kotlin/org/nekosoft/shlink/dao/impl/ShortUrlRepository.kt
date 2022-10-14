package org.nekosoft.shlink.dao.impl

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.nekosoft.shlink.entity.ShortUrl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
@JaversSpringDataAuditable
interface ShortUrlRepository : JpaRepository<ShortUrl, Long> {

    @Query("SELECT n from ShortUrl n WHERE n.shortCode = :#{#meta.shortCode} AND n.domain = :#{#meta.domain} AND n.longUrl = :#{#meta.longUrl} AND n.maxVisits = :#{#meta.maxVisits}")
    fun findMatching(meta: ShortUrl): ShortUrl?

    fun existsByShortCodeAndDomainAuthority(shortCode: String, authority: String): Boolean

    fun findByShortCodeAndDomainAuthority(shortCode: String, authority: String): ShortUrl?

    @Query("SELECT s.shortCode from ShortUrl s WHERE s.crawlable = TRUE AND s.password IS NULL")
    fun findByCrawlableIsTrue(): List<String>

    @Query("SELECT s from ShortUrl s WHERE (s.shortCode = ?1) AND (s.domain.isDefault = TRUE OR s.domain.authority = ?2) ORDER BY s.domain.isDefault ASC")
    fun findEnabledByShortCodeAndDomainWithFallback(shortCode: String, domain: String): List<ShortUrl>

    @Modifying
    @Query("DELETE FROM ShortUrlsToTags t WHERE t.shortUrl = ?1")
    fun deleteAllTagLinks(shortUrl: ShortUrl)

}
