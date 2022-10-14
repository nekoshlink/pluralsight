package org.nekosoft.shlink.dao.impl

import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.entity.support.ShortUrlWithStats
import org.nekosoft.shlink.entity.support.VisitStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface VisitRepository : JpaRepository<Visit, Long> {

    @Query("SELECT NEW org.nekosoft.shlink.entity.support.VisitStats(SUM(CASE WHEN v.shortUrl IS NULL THEN 0 ELSE 1 END), SUM(CASE WHEN v.shortUrl IS NULL THEN 1 ELSE 0 END), SUM(CASE WHEN v.type = 0 THEN 1 ELSE 0 END), SUM(CASE WHEN v.type = 2 THEN 1 ELSE 0 END), SUM(CASE WHEN v.type = 3 OR v.type = 4 THEN 1 ELSE 0 END), SUM(CASE WHEN v.type = 5 THEN 1 ELSE 0 END), SUM(CASE WHEN v.type = 1 OR v.type = 6 OR v.type = 7 THEN 1 ELSE 0 END)) FROM Visit v")
    fun visitStats(): VisitStats

    @Query("SELECT NEW org.nekosoft.shlink.entity.support.ShortUrlWithStats(s, SUM(1), SUM(CASE WHEN v.type = 0 THEN 1 ELSE 0 END), SUM(CASE WHEN v.type = 2 THEN 1 ELSE 0 END), SUM(CASE WHEN v.type = 3 OR v.type = 4 THEN 1 ELSE 0 END), SUM(CASE WHEN v.type = 5 THEN 1 ELSE 0 END), SUM(CASE WHEN v.type = 1 OR v.type = 6 OR v.type = 7 THEN 1 ELSE 0 END)) FROM ShortUrl s LEFT JOIN Visit v ON v.shortUrl = s WHERE s.id = :#{#shortUrl.id} GROUP BY s.id")
    fun visitStatsPerShortUrl(shortUrl: ShortUrl): ShortUrlWithStats

    @Query("UPDATE Visit SET shortUrl = NULL WHERE shortUrl.id = :#{#shortUrlId}")
    @Modifying
    fun setShortUrlToNull(shortUrlId: Long)

}
