package org.nekosoft.shlink.sec.user.impl

import org.nekosoft.shlink.sec.user.Role
import org.nekosoft.shlink.sec.user.ShlinkPermission
import org.nekosoft.shlink.sec.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {

    fun findByPermissionAndUser(ordinal: ShlinkPermission, user: User): Role?

}
