package org.nekosoft.shlink.sec.user

import org.nekosoft.shlink.sec.user.rest.UserListOptions
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetailsService

/*
 * Our data access implements the UserDetailsService interface for interoperability with Spring Security, but it is
 * effectively a DAO object subject to authorization constraints
 */
interface UserDataAccess : UserDetailsService {

    override fun loadUserByUsername(username: String): NekoShlinkUserDetails

    fun loadUserByApiKey(apiKey: String): NekoShlinkUserDetails

    /**
     * Get a list of all the users
     */
    fun loadAllUsers(options: UserListOptions, pageable: Pageable?): Page<NekoShlinkUserDetails>

    /**
     * Create a new user with the supplied details.
     */
    fun createUser(user: User): User

    /**
     * Update the specified user.
     */
    fun updateUser(user: User): User

    /**
     * Remove the user with the given login name from the system.
     */
    fun deleteUser(username: String)

    /**
     * Modify the current user's password. This should change the user's password in the
     * persistent user repository (datbase, LDAP etc).
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    fun changePassword(username: String, oldPassword: String, newPassword: String)

    /**
     * Add a legacy API key to the user
     */
    fun addApiKey(username: String, key: String?)

    /**
     * Remove the legacy API key from the user
     */
    fun removeApiKey(username: String)

    /**
     * Check if a user with the supplied login name exists in the system.
     */
    fun userExists(username: String): Boolean

}
