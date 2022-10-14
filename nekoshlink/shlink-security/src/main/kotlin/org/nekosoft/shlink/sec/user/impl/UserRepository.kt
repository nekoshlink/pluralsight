package org.nekosoft.shlink.sec.user.impl

import org.nekosoft.shlink.sec.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByUsernameAndEnabledIsTrue(username: String): User?

    fun findByLegacyApiKeyAndEnabledIsTrue(key: String): User?

    fun findByUsernameAndPasswordAndEnabledIsTrue(username: String, password: String): User?

    fun deleteByUsername(username: String)

}