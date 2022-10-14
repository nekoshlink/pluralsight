package org.nekosoft.shlink.entity.support

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.entity.Tag
import javax.persistence.*

@Entity
data class ShortUrlsToTags(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JsonIgnore // this is never needed via JSON
    var shortUrl: ShortUrl?,

    @ManyToOne
    @JsonIgnore // this is never needed via JSON
    var tag: Tag,

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ShortUrlsToTags

        return if (id != null) {
            id == other.id
        } else {
            shortUrl == other.shortUrl && tag == other.tag
        }
    }

    override fun hashCode(): Int =
        id?.hashCode() ?: (shortUrl.hashCode() + tag.hashCode())

    @Override
    override fun toString(): String =
        this::class.simpleName + "(id = $id, shortUrl = $shortUrl, tag = $tag)"

}