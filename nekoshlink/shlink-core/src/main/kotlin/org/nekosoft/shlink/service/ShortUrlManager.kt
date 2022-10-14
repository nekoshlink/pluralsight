package org.nekosoft.shlink.service

import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.support.ShortUrlWithStats
import org.nekosoft.shlink.service.exception.NekoShlinkResolutionException
import org.nekosoft.shlink.vo.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.util.UriComponents
import java.io.ByteArrayOutputStream
import kotlin.jvm.Throws

interface ShortUrlManager {
    fun retrieve(options: ShortUrlRetrieveOptions): ShortUrl
    fun create(meta: ShortUrlCreateMeta, options: ShortUrlCreateOptions): ShortUrl
    fun edit(meta: ShortUrlEditMeta, options: ShortUrlEditOptions): ShortUrl
    fun delete(options: ShortUrlEditOptions)
    fun list(options: ShortUrlListOptions, pageable: Pageable? = null): Page<ShortUrl>
    fun listWithStats(options: ShortUrlListOptions, pageable: Pageable? = null): Page<ShortUrlWithStats>
    fun listCrawlableURLs(): List<String>
    fun resolve(meta: ResolveMeta, enricher: VisitDataEnricher = noopVisitDataEnricher): Pair<ShortUrl?, UriComponents?>
    fun qrResolve(meta: ResolveMeta, options: QRCodeOptions, enricher: VisitDataEnricher): ByteArrayOutputStream?
}
