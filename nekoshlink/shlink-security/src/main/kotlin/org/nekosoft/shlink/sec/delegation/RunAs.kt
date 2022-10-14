package org.nekosoft.shlink.sec.delegation

import org.springframework.security.access.intercept.RunAsUserToken
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder


/**
 * Allows the execution of code under specified roles until the end of the resource block. It should be invoked like this
 *
 * ```
 * RunAs.userWithRoles(VISITS_ROLE). use {
 *   visits.recordNewVisit()
 * }
 * ```
 * and it can also be used in as an expression, like this
 *
 * ```
 * val results = RunAs.userWithRoles(VISITS_ROLE). use {
 *   visits.getVisits()
 * }
 * ```
 * The [userWithRoles] method expects that there is already an authenticated user in the Spring Security Context, otherwise
 * it will not do anything and no escalated permissions will be assigned. If you want escalated permissions to be assigned
 * whether there is already an authenticated user or not, you should use the [anonymousWithRoles] method.
 */
class RunAs private constructor(private val originalAuthentication: Authentication?) : AutoCloseable {

    private val rolePrefix = "ROLE_"

    private fun prepare(anonymous: Boolean, vararg roles: String): RunAs {
        val isAuthenticated = (originalAuthentication != null) && originalAuthentication.isAuthenticated
        if (anonymous || (isAuthenticated && originalAuthentication !is AnonymousAuthenticationToken)) {
            val newAuths = roles
                .map { if (it.startsWith(rolePrefix)) it else "$rolePrefix$it" }
                .map { SimpleGrantedAuthority(it) as GrantedAuthority }
                .toMutableList()
            if (isAuthenticated) newAuths.addAll(originalAuthentication!!.authorities)
            val token = if (isAuthenticated) {
                RunAsUserToken(
                    "nekoshlink-runas-role-key",
                    originalAuthentication!!.principal,
                    originalAuthentication.credentials,
                    newAuths,
                    originalAuthentication.javaClass,
                )
            } else {
                AnonymousAuthenticationToken(
                    "nekoshlink-runas-role-key",
                    originalAuthentication?.principal ?: "anonymous",
                    newAuths,
                )
            }
            SecurityContextHolder.getContext().authentication = token
        }
        return this
    }

    override fun close() {
        SecurityContextHolder.getContext().authentication = originalAuthentication
    }

    companion object {

        /**
         * Will run the code following this, until the end of the resource block, with the given roles.
         * If there is no authenticated principal at the moment of the call, this will not do anything and no
         * delegated privileges will be assigned, otherwise it will add the roles to the existing authenticated principal.
         */
        fun userWithRoles(vararg roles: String): RunAs {
            val orgAuth: Authentication = SecurityContextHolder.getContext().authentication
            return RunAs(orgAuth).prepare(false, *roles)
        }

        /**
         * Will run the code following this, until the end of the resource block, with the given roles.
         * If there is no authenticated principal at the moment of the call, this will create a new anonymous
         * user with the given roles, otherwise it will add the roles to the existing authenticated principal.
         */
        fun anonymousWithRoles(vararg roles: String): RunAs {
            val orgAuth: Authentication = SecurityContextHolder.getContext().authentication
            return RunAs(orgAuth).prepare(true, *roles)
        }
    }

}
