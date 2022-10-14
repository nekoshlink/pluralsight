package org.nekosoft.shlink.rest.controller

import org.nekosoft.shlink.service.ShortUrlManager
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/*
 *  https://technicalseo.com/tools/robots-txt/
 */

@RestController
class RobotsController(private val shortUrls: ShortUrlManager) {

    // no need for pre-authorize here as the security filter requires no authorization for this controller
    @GetMapping("robots.txt", produces = ["text/plain"])
    fun listCrawlable(): ResponseEntity<String> {
        val prefix = """
        # For more information about the robots.txt standard, see:
        # https://www.robotstxt.org/orig.html

        User-agent: *

        """.trimIndent()

        val allowed = shortUrls.listCrawlableURLs().map { "Allow: /$it" }.joinToString("\n")

        val suffix = """
        
            Disallow: /
        """.trimIndent()

        return ResponseEntity.ok("$prefix\n$allowed\n$suffix\n")
    }

}
