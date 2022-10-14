package org.nekosoft.shlink.sec.user

import com.fasterxml.jackson.annotation.JsonBackReference
import org.hibernate.Hibernate
import javax.persistence.*

@Entity
data class Role(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JsonBackReference
    var user: User? = null,

    var permission: ShlinkPermission = ShlinkPermission.Viewer,

    ) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Role

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
        return this::class.simpleName + "(id = $id, user = ${user?.username}, permission = $permission)"
    }

}
