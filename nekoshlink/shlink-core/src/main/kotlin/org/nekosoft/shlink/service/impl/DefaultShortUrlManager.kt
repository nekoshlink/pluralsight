package org.nekosoft.shlink.service.impl

import io.github.g0dkar.qrcode.QRCode
import io.github.g0dkar.qrcode.render.Colors
import org.hashids.Hashids
import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.dao.ShortUrlDataAccess
import org.nekosoft.shlink.dao.TagDataAccess
import org.nekosoft.shlink.dao.VisitDataAccess
import org.nekosoft.shlink.entity.*
import org.nekosoft.shlink.entity.support.ShortUrlWithStats
import org.nekosoft.shlink.entity.support.ShortUrlsToTags
import org.nekosoft.shlink.entity.support.VisitType
import org.nekosoft.shlink.sec.delegation.annotation.RunAs
import org.nekosoft.shlink.sec.delegation.RunAs as RunAsBlock
import org.nekosoft.shlink.service.*
import org.nekosoft.shlink.service.exception.*
import org.nekosoft.shlink.vo.*
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.LocalDateTime
import java.util.random.RandomGeneratorFactory
import javax.transaction.Transactional

@Service
class DefaultShortUrlManager(
    private val dao: ShortUrlDataAccess,
    private val domains: DomainDataAccess,
    private val visits: VisitDataAccess,
    private val tags: TagDataAccess,
    private val tracker: VisitTracker,
) : ShortUrlManager {

    override fun retrieve(options: ShortUrlRetrieveOptions): ShortUrl {
        if ((options.id != null) xor (options.shortCode != null)) {
            val shortUrl = if (options.id != null) {
                dao.findById(options.id!!)
            } else {
                val authority = RunAsBlock.userWithRoles("Domains", "Viewer").use {
                    domains.resolveDefaultAuthority(options.domain)
                }
                dao.find(options.shortCode!!, authority)
            }
            if (shortUrl == null) {
                throw ShortUrlDoesNotExistException(options.id, options.shortCode, options.domain)
            }
            return shortUrl
        } else {
            throw FindOptionsException()
        }
    }

    @Transactional
    override fun create(meta: ShortUrlCreateMeta, options: ShortUrlCreateOptions): ShortUrl {

        if (meta.longUrl == null) throw MissingLongUrlException()

        val domain = RunAsBlock.userWithRoles("Domains", "Viewer").use {
            domains.findByAuthority(meta.domain) ?: throw DomainDoesNotExistException(domains.resolveDefaultAuthority(meta.domain))
        }

        val customSlugWasProvided = (meta.shortCode != null)
        if (!customSlugWasProvided) {
            meta.shortCode = shorten(options.shortCodeLength)
        }

        val shortUrl = ShortUrl(
            shortCode = meta.shortCode!!,
            longUrl = meta.longUrl!!,
            validFrom = meta.validFrom,
            validUntil = meta.validUntil,
            maxVisits = if (meta.maxVisits != null && meta.maxVisits!! <= 0) { 0 } else { meta.maxVisits },
            domain = domain,
            customSlugWasProvided = customSlugWasProvided,
            title = meta.title,
            crawlable = meta.crawlable,
            forwardQuery = meta.forwardQuery,
            forwardPathTrail = meta.forwardPathTrail,
            password = meta.password,
        )
        resolveTagRelationship(meta.tags ?: emptyList(), shortUrl)

        val existingShortUrl = if (!options.findIfExists) null else dao.findMatching(shortUrl)
        if (existingShortUrl != null) return existingShortUrl

        shortUrl.titleWasAutoResolved = if (options.validateUrl) {
            if (options.useValidatedTitle && shortUrl.title == null) {
                val resolvedTitle = validateWithTitle(shortUrl.longUrl) ?: throw InvalidLongUrlException(shortUrl.longUrl)
                shortUrl.title = resolvedTitle
                true
            } else {
                if (validate(shortUrl.longUrl)) {
                    throw InvalidLongUrlException(shortUrl.longUrl)
                }
                false
            }
        } else {
            false
        }

        if (shortUrl.shortCode.length < ShortUrl.MIN_LENGTH) {
            throw InvalidShortCodeException(shortUrl.shortCode)
        }

        if (dao.shortCodeIsUnique(shortUrl)) {
            throw DuplicateShortCodeException(shortUrl.shortCode, shortUrl.domain.authority)
        }

        return dao.create(shortUrl)

    }

    @Transactional
    override fun edit(meta: ShortUrlEditMeta, options: ShortUrlEditOptions): ShortUrl {
        val shortUrl = retrieve(
            ShortUrlRetrieveOptions(options.id, options.shortCode, options.domain)
        )
        shortUrl.longUrl = meta.longUrl!!
        shortUrl.validFrom = meta.validFrom
        shortUrl.validUntil = meta.validUntil
        shortUrl.maxVisits = if (meta.maxVisits != null && meta.maxVisits!! <= 0) { 0 } else { meta.maxVisits }
        shortUrl.title = meta.title
        shortUrl.crawlable = meta.crawlable
        shortUrl.forwardQuery = meta.forwardQuery
        shortUrl.password = meta.password
        resolveTagRelationship(meta.tags ?: emptyList(), shortUrl)
        return dao.update(shortUrl)
    }

    @Transactional
    override fun delete(options: ShortUrlEditOptions) {
        if ((options.id != null) xor (options.shortCode != null)) {
            if (options.id != null) {
                try {
                    RunAsBlock.userWithRoles("Visits", "Editor").use {
                        visits.removeShortUrlReference(options.id!!)
                    }
                    dao.deleteById(options.id!!)
                } catch (_: EmptyResultDataAccessException) {
                    throw ShortUrlDoesNotExistException(options.id, options.shortCode, options.domain)
                }
            } else {
                val authority = RunAsBlock.userWithRoles("Domains", "Viewer").use {
                    domains.resolveDefaultAuthority(options.domain)
                }
                val shortUrl = dao.find(options.shortCode!!, authority) ?: throw ShortUrlDoesNotExistException(options.id, options.shortCode, authority)
                RunAsBlock.userWithRoles("Visits", "Editor").use {
                    visits.removeShortUrlReference(shortUrl.id!!)
                }
                dao.delete(shortUrl)
            }
        } else {
            throw FindOptionsException()
        }
    }

    override fun list(options: ShortUrlListOptions, pageable: Pageable?): Page<ShortUrl> {
        return dao.list(pageable)
    }

    override fun listWithStats(options: ShortUrlListOptions, pageable: Pageable?): Page<ShortUrlWithStats> {
        return dao.listWithStats(options, pageable)
    }

    @RunAs("ShortUrls", "Viewer", allowAnonymous = true)
    override fun listCrawlableURLs(): List<String> {
        return dao.listCrawlableURLs()
    }

    @RunAs("Visits", "Editor", allowAnonymous = true)
    override fun resolve(meta: ResolveMeta, enricher: VisitDataEnricher): Pair<ShortUrl?, UriComponents?> {
        if (meta.shortCode == null) {
            tracker.track(VisitType.INVALID_REQUEST, meta, null, null, enricher)
            throw MissingShortUrlException()
        }

        val shortUrl = RunAsBlock.anonymousWithRoles("ShortUrls", "Viewer").use {
            dao.findWithDomainFallback(meta.shortCode!!, meta.domain)
        }
        if (shortUrl != null) {

            if (shortUrl.password != meta.password) {
                tracker.track(VisitType.FORBIDDEN, meta, shortUrl, null, enricher)
                throw ProtectedShortUrlResolutionException(meta.shortCode!!, meta.domain)
            }
            val now = LocalDateTime.now()
            if (shortUrl.validUntil != null && now.isAfter(shortUrl.validUntil)) {
                tracker.track(VisitType.EXPIRED, meta, shortUrl, null, enricher)
                throw ShortUrlHasExpiredException(meta.shortCode!!, meta.domain)
            }
            if (shortUrl.validFrom != null && now.isBefore(shortUrl.validFrom)) {
                tracker.track(VisitType.DISABLED, meta, shortUrl, null, enricher)
                throw ShortUrlNotEnabledYetException(meta.shortCode!!, meta.domain)
            }
            if (shortUrl.maxVisits != null && shortUrl.maxVisits!! > 0) {
                val visitCount = RunAsBlock.userWithRoles("Visits", "Viewer").use {
                    visits.visitStatsPerShortUrl(shortUrl).visitCount
                }
                if (visitCount >= shortUrl.maxVisits!!) {
                    tracker.track(VisitType.OVER_LIMIT, meta, shortUrl, null, enricher)
                    throw MaxVisitLimitReachedException(meta.shortCode!!, meta.domain)
                }
            }

            val uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(shortUrl.longUrl)
            if (meta.pathTrail != null) {
                if (shortUrl.forwardPathTrail) {
                    uriComponentsBuilder.path("/").path(meta.pathTrail!!)
                } else {
                    tracker.track(VisitType.PATH_TRAIL_ERROR, meta, shortUrl, uriComponentsBuilder.build(), enricher)
                    throw PathTrailNotAllowedException(meta.shortCode!!, meta.domain, meta.pathTrail!!)
                    //return ResponseEntity.badRequest().body("Path trail is not allowed for this Short URL")
                }
            }
            if (meta.queryParams != null && meta.queryParams!!.isNotEmpty()) {
                if (shortUrl.forwardQuery) {
                    val queryParams = LinkedMultiValueMap<String, String>()
                    for (entry in meta.queryParams!!) {
                        queryParams[entry.key] = entry.value.toList()
                    }
                    uriComponentsBuilder.queryParams(queryParams)
                } else {
                    tracker.track(VisitType.QUERY_PARAMS_ERROR, meta, shortUrl, uriComponentsBuilder.build(), enricher)
                    throw QueryParamsNotAllowedException(meta.shortCode!!, meta.domain, meta.queryParams.toString())
                    //return ResponseEntity.badRequest().body("Path trail is not allowed for this Short URL")
                }
            }

            val uriComponents = uriComponentsBuilder.build()
            tracker.track(VisitType.SUCCESSFUL, meta, shortUrl, uriComponents, enricher)
            return Pair(shortUrl, uriComponents)
        }

        tracker.track(VisitType.ORPHAN, meta, null, null, enricher)
        return Pair(null, null)
    }

    // https://qrcodekotlin.com
    override fun qrResolve(meta: ResolveMeta, options: QRCodeOptions, enricher: VisitDataEnricher): ByteArrayOutputStream? {
        val (shortUrl, uriComponents) = resolve(meta, enricher)
        if (shortUrl == null) return null
        val newUriComponents = UriComponentsBuilder.fromUriString(shortUrl.shortUrl)
            .path(meta.pathTrail ?: "")
            .queryParams(uriComponents?.queryParams)
            .build()
        val imageOut = ByteArrayOutputStream()
        // calling toUri() first ensures characters are properly URL-encoded before the string is generated
        QRCode(newUriComponents.toUri().toString())
            .render(
                margin = 25,
                cellSize = 25,
                brightColor = Colors.css("#8b949e"),
                darkColor = Colors.css("#0d1117"),
            )
            .writeImage(imageOut)
        return imageOut
    }

    private fun resolveTagRelationship(tagNames: List<String>, shortUrl: ShortUrl) {
        shortUrl.tags.retainAll {
            if (it.tag.name in tagNames) {
                true
            } else {
                it.shortUrl = null
                false
            }
        }
        // DO NOT DELEGATE AS TAGS EDITOR
        // If the user is not an editor, they can only assign existing tags
        // but they should not be able to create new ones
        RunAsBlock.userWithRoles("Tags", "Viewer").use {
            tagNames.forEach {
                if (shortUrl.tags.find { t -> t.tag.name == it } == null) {
                    val tag = tags.findByName(it) ?: tags.create(it, null)
                    shortUrl.tags.add(ShortUrlsToTags(null, shortUrl, tag))
                }
            }
        }
   }

    // Variables used to create the short code for a Short URL
    private var hashids: Hashids = Hashids("nekoshlink-shortcode-salt")
    private val random = RandomGeneratorFactory.getDefault().create()

    // Method that creates a short code of a given length
    private fun shorten(length: Int): String {
        if (length < ShortUrl.MIN_LENGTH) throw MinimumShortCodeLengthException(length)
        val hash = hashids.encode(random.nextLong(Hashids.MAX_NUMBER), ShortUrl.MIN_LENGTH.toLong())
        val reducedHash = if (hash.length <= length) hash else {
            val startPos = random.nextInt(hash.length - length)
            hash.substring(startPos, startPos + length)
        }
        return reducedHash
    }

    private fun validate(url: String): Boolean {
        return connectForResponse(url) != null
    }

    private fun validateWithTitle(url: String): String? {
        val body = connectForResponse(url)
        return if (body == null) null else {
            val regex = """<title[^>]*>(.*?)</title>""".toRegex()
            return regex.find(body)?.groups?.get(1)?.value ?: ""
        }
    }

    private fun connectForResponse(url: String): String? {
        val uri = try {
            URI.create(url)
        } catch (e: IllegalArgumentException) {
            return null
        }
        val httpClient: HttpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(9))
            .build()
        val requestHead = HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .build()
        val httpResponse = try {
            httpClient.send(requestHead, HttpResponse.BodyHandlers.ofString())
        } catch (e: IOException) {
            return null
        }
        return if (httpResponse.statusCode() in 200..299) httpResponse.body() else null
    }

}
