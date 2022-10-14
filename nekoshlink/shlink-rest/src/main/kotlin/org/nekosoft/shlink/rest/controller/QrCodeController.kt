package org.nekosoft.shlink.rest.controller

import org.nekosoft.shlink.entity.ShortUrl.Companion.URL_SEGMENT_REGEX
import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.service.ShortUrlManager
import org.nekosoft.shlink.service.VisitDataEnricher
import org.nekosoft.shlink.vo.QRCodeOptions
import org.nekosoft.shlink.vo.ResolveMeta
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.IMAGE_PNG
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/qr/{shortCode:$URL_SEGMENT_REGEX}")
class QrCodeController(
    private val shortUrls: ShortUrlManager,
) : AbstractResolveController<ByteArrayResource>() {

    override fun getSource() = VisitSource.REST_QR

    override fun getResponse(meta: ResolveMeta, enricher: VisitDataEnricher, request: HttpServletRequest): ResponseEntity<ByteArrayResource> {
        val options = QRCodeOptions(
            filename = request.getParameter("filename"),
            size = request.getParameter("size")?.toIntOrNull(),
        )
        val imageOut = shortUrls.qrResolve(meta, options, enricher) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        val resource = ByteArrayResource(imageOut.toByteArray(), IMAGE_PNG_VALUE)

        return ResponseEntity.ok()
            .header(CONTENT_DISPOSITION, "attachment; filename=\"${options.filename}\"")
            .contentType(IMAGE_PNG)
            .body(resource)
    }

}
