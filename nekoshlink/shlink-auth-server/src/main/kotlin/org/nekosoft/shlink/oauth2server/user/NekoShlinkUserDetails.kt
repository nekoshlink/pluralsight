package org.nekosoft.shlink.oauth2server.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class NekoShlinkUserDetails(val user: User) : UserDetails {

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        user.roles.map { SimpleGrantedAuthority("ROLE_${it.permission.name}") }.toMutableSet()

    /**
     * Returns the password used to authenticate the user.
     * @return the password
     */
    override fun getPassword(): String =
        user.password ?: ""

    /**
     * Returns the username used to authenticate the user. Cannot return
     * `null`.
     * @return the username (never `null`)
     */
    override fun getUsername(): String =
        user.username

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     * @return `true` if the user's account is valid (ie non-expired),
     * `false` if no longer valid (ie expired)
     */
    override fun isAccountNonExpired(): Boolean =
        true

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     * @return `true` if the user is not locked, `false` otherwise
     */
    override fun isAccountNonLocked(): Boolean =
        true

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     * @return `true` if the user's credentials are valid (ie non-expired),
     * `false` if no longer valid (ie expired)
     */
    override fun isCredentialsNonExpired(): Boolean =
        true

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     * @return `true` if the user is enabled, `false` otherwise
     */
    override fun isEnabled(): Boolean =
        user.enabled

}
