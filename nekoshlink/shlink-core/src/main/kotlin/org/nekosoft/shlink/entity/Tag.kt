package org.nekosoft.shlink.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import org.nekosoft.shlink.entity.support.AuditInfo
import org.nekosoft.shlink.entity.support.JpaDataAccessAudit
import org.nekosoft.shlink.entity.support.ShortUrlsToTags
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class, JpaDataAccessAudit::class)
data class Tag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true)
    var name: String,

    var description: String? = null,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var shortUrls: MutableSet<ShortUrlsToTags> = mutableSetOf(),

    @Embedded
    var auditInfo: AuditInfo = AuditInfo(),

    ) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Tag

        return if (id != null) {
            id == other.id
        } else {
            name == other.name
        }
    }

    override fun hashCode(): Int =
        id?.hashCode() ?: name.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = $name)"
    }

}
