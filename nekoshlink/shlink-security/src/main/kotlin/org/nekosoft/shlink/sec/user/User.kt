package org.nekosoft.shlink.sec.user

import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.Hibernate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var dateCreated: LocalDateTime = LocalDateTime.now(),

    var lastModifiedDate: LocalDateTime? = null,

    @Column(unique = true)
    var username: String = "",

    var firstName: String? = null,

    var lastName: String? = null,

    var description: String? = null,

    var enabled: Boolean = true,

    var password: String? = null,

    @Column(unique = true)
    var legacyApiKey: String? = null,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    var roles: MutableSet<Role> = mutableSetOf(),

    ) : java.io.Serializable { // must be serializable so it can be stored in security context

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return if (id != null) {
            id == other.id
        } else {
            username == other.username
        }
    }

    override fun hashCode(): Int =
        id?.hashCode() ?: username.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, username = $username)"
    }

}
