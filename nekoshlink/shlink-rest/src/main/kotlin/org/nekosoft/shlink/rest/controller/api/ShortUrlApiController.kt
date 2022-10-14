package org.nekosoft.shlink.rest.controller.api

import org.nekosoft.shlink.dao.VisitDataAccess
import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.ShortUrl.Companion.URL_SEGMENT_REGEX
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.entity.support.ShortUrlWithStats
import org.nekosoft.shlink.rest.ShlinkRestApiServer
import org.nekosoft.shlink.vo.rest.PaginationData
import org.nekosoft.shlink.vo.rest.RestResult
import org.nekosoft.shlink.service.ShortUrlManager
import org.nekosoft.shlink.vo.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("api/v${ShlinkRestApiServer.VERSION_STRING}/shorturls") // this short mapping guarantees it cannot be a short code (min five characters)
class ShortUrlApiController(
    private val shortUrls: ShortUrlManager,
    private val visits: VisitDataAccess,
) {

    @GetMapping
    fun shortUrls(options: ShortUrlListOptions, pagination: PaginationOptions): ResponseEntity<RestResult<ShortUrlWithStats>> {
        val results = shortUrls.listWithStats(options, PaginationData.paginationToPageable(pagination))
        return ResponseEntity.status(HttpStatus.OK).body(RestResult(
            results = results.content,
            pagination = PaginationData.fromPage(results)
        ))
    }

    @GetMapping("{shortCode:$URL_SEGMENT_REGEX}")
    fun shortUrl(@PathVariable("shortCode") shortCode: String, @RequestParam("domain", required = false) domain: String?): ResponseEntity<ShortUrl> {
        val shortUrl = shortUrls.retrieve(
                ShortUrlRetrieveOptions(
                    shortCode = shortCode,
                    domain = domain,
                )
            )
        return ResponseEntity.ok(shortUrl)
    }

    @GetMapping("{shortCode:$URL_SEGMENT_REGEX}/visits")
    fun getVisitsForShortUrl(
        @PathVariable("shortCode") shortCode: String,
        options: VisitListOptions,
        pagination: PaginationOptions
    ): ResponseEntity<RestResult<Visit>> {
        options.shortCode = shortCode
        val results = visits.getVisits(options, PaginationData.paginationToPageable(pagination))
        return ResponseEntity.status(HttpStatus.OK).body(RestResult(
            results = results.content,
            pagination = PaginationData.fromPage(results)
        ))
    }

    @PostMapping
    fun createShortUrl(@RequestBody meta: ShortUrlCreateMeta, options: ShortUrlCreateOptions): ResponseEntity<ShortUrl> {
        val shortUrl = shortUrls.create(meta, options)
        return ResponseEntity.status(HttpStatus.OK).body(shortUrl)
    }

    @PutMapping("{shortCode:$URL_SEGMENT_REGEX}")
    fun editShortUrl(@PathVariable("shortCode") shortCode: String, @RequestBody meta: ShortUrlEditMeta, options: ShortUrlEditOptions): ResponseEntity<ShortUrl> {
        options.shortCode = shortCode
        val shortUrl = shortUrls.edit(meta, options)
        return ResponseEntity.status(HttpStatus.OK).body(shortUrl)
    }

    @DeleteMapping("{shortCode:$URL_SEGMENT_REGEX}")
    fun deleteShortUrl(@PathVariable("shortCode") shortCode: String, @RequestParam("domain", required = false) domain: String?): ResponseEntity<Void> {
        shortUrls.delete(
            ShortUrlEditOptions(
                shortCode = shortCode,
                domain = domain,
            )
        )
        /*
         * AS PER RFC 7231 (https://www.rfc-editor.org/rfc/rfc7231)
         * If a DELETE method is successfully applied, the origin server SHOULD send a 202 (Accepted) status code
         * if the action will likely succeed but has not yet been enacted, a 204 (No Content) status code if the action
         * has been enacted and no further information is to be supplied, or a 200 (OK) status code if the action has
         * been enacted and the response message includes a representation describing the status.
         */
        return ResponseEntity.noContent().build()
    }

}
