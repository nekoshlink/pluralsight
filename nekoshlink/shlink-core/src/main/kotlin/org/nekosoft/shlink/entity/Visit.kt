package org.nekosoft.shlink.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.Hibernate
import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.entity.support.VisitType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Visit(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    var shortUrl: ShortUrl? = null,

    var shortCode: String? = null,

    var domain: String? = null,

    var type: VisitType = VisitType.SUCCESSFUL,

    var source: VisitSource = VisitSource.API,

    var visitedUrl: String? = null,

    var date: LocalDateTime = LocalDateTime.now(),

    var referrer: String? = null,

    var remoteAddr: String? = null,

    var pathTrail: String? = null,

    var queryString: String? = null,

    var userAgent: String? = null,

    ) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Visit

        return if (id != null) {
            id == other.id
        } else {
            false
        }
    }

    override fun hashCode(): Int =
        id?.hashCode() ?: super.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, shortCode = $shortCode, date = $date)"
    }

}
