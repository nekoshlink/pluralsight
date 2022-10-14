package org.nekosoft.shlink.rest.controller

import org.nekosoft.shlink.entity.ShortUrl.Companion.URL_SEGMENT_REGEX
import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.service.ShortUrlManager
import org.nekosoft.shlink.service.VisitDataEnricher
import org.nekosoft.shlink.vo.ResolveMeta
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/tk/{shortCode:$URL_SEGMENT_REGEX}")
class TrackController(
    private val shortUrls: ShortUrlManager,
) : AbstractResolveController<ByteArrayResource>() {

    override fun getSource() = VisitSource.REST_TRACK

    override fun getResponse(meta: ResolveMeta, enricher: VisitDataEnricher, request: HttpServletRequest): ResponseEntity<ByteArrayResource> {
        val (shortUrl, _) = shortUrls.resolve(meta, enricher)
        if (shortUrl == null) {
            return ResponseEntity.notFound().build()
        }
        val decodedBytes = Base64.getDecoder().decode("R0lGODlhAQABAJAAAP8AAAAAACH5BAUQAAAALAAAAAABAAEAAAICBAEAOw==")
        val resource = ByteArrayResource(decodedBytes, IMAGE_GIF_VALUE)

        return ResponseEntity.ok()
            .header(CONTENT_DISPOSITION, "inline")
            .contentType(IMAGE_GIF)
            .body(resource)
    }

}
