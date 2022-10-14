package org.nekosoft.shlink.service.impl

import org.nekosoft.shlink.dao.VisitDataAccess
import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.entity.support.VisitType
import org.nekosoft.shlink.service.VisitDataEnricher
import org.nekosoft.shlink.service.VisitTracker
import org.nekosoft.shlink.vo.ResolveMeta
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponents

@Component
class DefaultVisitTracker(private val visits: VisitDataAccess) : VisitTracker {

    override fun track(type: VisitType, meta: ResolveMeta, shortUrl: ShortUrl?, uriComponents: UriComponents?, enricher: VisitDataEnricher) {
        val visit = Visit(
            shortUrl = shortUrl,
            shortCode = meta.shortCode,
            domain = meta.domain,
            type = type,
            source = VisitSource.API,
            visitedUrl = uriComponents.toString(),
        )
        val enrichedData = enricher()
        if (enrichedData != null) {
            visit.source = enrichedData.source
            visit.referrer = enrichedData.referrer
            visit.remoteAddr = enrichedData.remoteAddr
            visit.pathTrail = enrichedData.pathTrail
            visit.queryString = enrichedData.queryString
            visit.userAgent = enrichedData.userAgent
        }
        visits.add(visit)
    }

}
