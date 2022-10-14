package org.nekosoft.shlink.rest.controller.api

import org.nekosoft.shlink.audit.AuditBrowserService
import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.dao.TagDataAccess
import org.nekosoft.shlink.entity.Domain
import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.Tag
import org.nekosoft.shlink.rest.ShlinkRestApiServer
import org.nekosoft.shlink.service.ShortUrlManager
import org.nekosoft.shlink.vo.ShortUrlRetrieveOptions
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("aud/v${ShlinkRestApiServer.VERSION_STRING}") // this short mapping guarantees it cannot be a short code (min five characters)
class AuditController(
    val domains: DomainDataAccess,
    val shortUrls: ShortUrlManager,
    val tags: TagDataAccess,
    val audit: AuditBrowserService,
) {

    @GetMapping("domains/{authority}")
    fun getDomainChanges(@PathVariable authority: String): List<Domain> {
        val domain = domains.findByAuthority(authority)
        return getDomainChanges(domain!!.id!!)
    }

    @GetMapping("shorturls/{shortCode}")
    fun getShortUrlChanges(@PathVariable shortCode: String, @RequestParam("domain") domain: String?): List<ShortUrl> {
        val shortUrl = shortUrls.retrieve(
            ShortUrlRetrieveOptions(shortCode = shortCode, domain = domain)
        )
        return getShortUrlChanges(shortUrl.id!!)
    }

    @GetMapping("tags/{name}")
    fun getTagChanges(@PathVariable name: String): List<Tag> {
        val tag = tags.findByName(name)
        return getTagChanges(tag!!.id!!)
    }

    @GetMapping("domains/{id}")
    fun getDomainChanges(@PathVariable id: Long): List<Domain> =
        audit.getChanges(Domain::class.java, id)

    @GetMapping("shorturls/{id}")
    fun getShortUrlChanges(@PathVariable id: Long): List<ShortUrl> =
        audit.getChanges(ShortUrl::class.java, id)

    @GetMapping("tags/{id}")
    fun getTagChanges(@PathVariable id: Long): List<Tag> {
        return audit.getChanges(Tag::class.java, id)
    }
}