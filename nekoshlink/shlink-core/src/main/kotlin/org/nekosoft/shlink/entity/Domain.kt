package org.nekosoft.shlink.entity

import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import org.nekosoft.shlink.entity.support.AuditInfo
import org.nekosoft.shlink.entity.support.JpaDataAccessAudit
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@Audited
@EntityListeners(AuditingEntityListener::class, JpaDataAccessAudit::class)
class Domain(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column(unique = true)
        var authority: String = DEFAULT_DOMAIN,

        var scheme: String = "https",

        // for compatibility with Shlink.io
        var baseUrlRedirect: String? = null,

        // for compatibility with Shlink.io
        var regular404Redirect: String? = null,

        // for compatibility with Shlink.io
        var invalidShortUrlRedirect: String? = null,

        var isDefault: Boolean = authority == DEFAULT_DOMAIN,

        @Embedded
        var auditInfo: AuditInfo = AuditInfo(),

) {

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
                other as Domain

                return if (id != null) {
                        id == other.id
                } else {
                        scheme == other.scheme && authority == other.authority
                }
        }

        override fun hashCode(): Int =
                id?.hashCode() ?: (scheme + authority).hashCode()

        @Override
        override fun toString(): String =
                this::class.simpleName + "(id = $id, scheme = $scheme, authority = $authority)"

        companion object {
                // make sure this string could not also work as a valid authority
                // e.g. by using invalid characters in a domain name such as < > # etc
                const val DEFAULT_DOMAIN = "<<#DEFAULT#>>"
        }
}
