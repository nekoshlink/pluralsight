package org.nekosoft.shlink.service

import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.entity.support.VisitType
import org.nekosoft.shlink.vo.ResolveMeta
import org.springframework.web.util.UriComponents

typealias VisitDataEnricher = () -> Visit?

val noopVisitDataEnricher: VisitDataEnricher = { null }

interface VisitTracker {
    fun track(type: VisitType, meta: ResolveMeta, shortUrl: ShortUrl?, uriComponents: UriComponents?, enricher: VisitDataEnricher)
}
