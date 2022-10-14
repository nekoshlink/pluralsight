package org.nekosoft.shlink.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import org.nekosoft.shlink.entity.support.AuditInfo
import org.nekosoft.shlink.entity.support.JpaDataAccessAudit
import org.nekosoft.shlink.entity.support.ShortUrlsToTags
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Audited
@EntityListeners(AuditingEntityListener::class, JpaDataAccessAudit::class)
class ShortUrl (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var shortCode: String,

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    var domain: Domain,

    var longUrl: String,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "shortUrl", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var tags: MutableSet<ShortUrlsToTags> = mutableSetOf(),

    var validFrom: LocalDateTime? = null,

    var validUntil: LocalDateTime? = null,

    var maxVisits: Int? = null,

    var customSlugWasProvided: Boolean = false,

    var importSource: String? = null,

    var importOriginalShortCode: String? = null,

    var authorApiKey: String? = null,

    var title: String? = null,

    var titleWasAutoResolved: Boolean = false,

    var crawlable: Boolean = false,

    var forwardQuery: Boolean = true,

    var forwardPathTrail: Boolean = true,

    var password: String? = null,

    @Embedded
    var auditInfo: AuditInfo = AuditInfo(),

    ) {

    val authority: String
        @Transient
        @JsonProperty("domain")
        get() = domain.authority

    val tagList: List<String>
        @Transient
        @JsonProperty("tags")
        get() = tags.map { it.tag.name }

    val shortUrl: String
        @Transient
        get() = makeShortUrl()

    val qrShortUrl: String
        @Transient
        get() = makeShortUrl("qr")

    val trackShortUrl: String
        @Transient
        get() = makeShortUrl("tk")

    fun makeShortUrl(prefix: String = ""): String {
        val domainPort = domain.authority.split(':', limit =  2)
        return UriComponentsBuilder.newInstance()
            .scheme(domain.scheme)
            .host(domainPort[0])
            .port(domainPort.getOrNull(1))
            .pathSegment(prefix)
            .path(shortCode)
            // calling toUri() first ensures characters are properly URL-encoded before the string is generated
            .build().toUri().toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ShortUrl

        return if (id != null) {
            id == other.id
        } else {
            shortCode == other.shortCode && authority == other.authority
        }
    }

    override fun hashCode(): Int =
        id?.hashCode() ?: (shortCode + authority).hashCode()

    @Override
    override fun toString(): String =
        this::class.simpleName + "(id = $id, shortCode = $shortCode, authority = $authority)"

    companion object {
        const val MIN_LENGTH = 5
        const val CHAR_CLASS = "[A-Za-z0-9\\-._~]"
        const val URL_SEGMENT_REGEX = "$CHAR_CLASS{$MIN_LENGTH,}"
    }

}
