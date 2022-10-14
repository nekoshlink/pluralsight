package org.nekosoft.shlink.oauth2server.user.impl

import org.nekosoft.shlink.oauth2server.user.Role
import org.nekosoft.shlink.oauth2server.user.ShlinkPermission
import org.nekosoft.shlink.oauth2server.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {

    fun findByPermissionAndUser(ordinal: ShlinkPermission, user: User): Role?

}
