package org.nekosoft.shlink.rest.controller

import org.nekosoft.shlink.entity.ShortUrl.Companion.URL_SEGMENT_REGEX
import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.service.ShortUrlManager
import org.nekosoft.shlink.service.VisitDataEnricher
import org.nekosoft.shlink.vo.ResolveMeta
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("{shortCode:$URL_SEGMENT_REGEX}")
class RedirectController(
    private val shortUrls: ShortUrlManager,
) : AbstractResolveController<String>() {

    override fun getSource(): VisitSource = VisitSource.REST

    override fun getResponse(meta: ResolveMeta, enricher: VisitDataEnricher, request: HttpServletRequest): ResponseEntity<String> {
        val (_, uriComponents) = shortUrls.resolve(meta, enricher)
        return if (uriComponents == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short URL could not be resolved")
        } else {
            ResponseEntity.status(HttpStatus.FOUND).location(uriComponents.toUri()).build()
        }
    }

}
