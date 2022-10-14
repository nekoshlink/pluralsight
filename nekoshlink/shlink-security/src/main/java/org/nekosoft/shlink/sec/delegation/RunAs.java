package org.nekosoft.shlink.sec.delegation;

import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;


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
public class RunAs implements AutoCloseable {

    private Authentication originalAuthentication;
    private String rolePrefix = "ROLE_";

    private RunAs(Authentication originalAuthentication) {
        this.originalAuthentication = originalAuthentication;
    }

    private RunAs prepare(boolean anonymous, String... roles) {
        boolean isAuthenticated = (originalAuthentication != null) && originalAuthentication.isAuthenticated();
        if (anonymous || (isAuthenticated && !(originalAuthentication instanceof AnonymousAuthenticationToken))) {
            List<GrantedAuthority> newAuths = new java.util.ArrayList<>(Arrays.stream(roles)
                    .map(it -> it.startsWith(rolePrefix) ? it : rolePrefix + it)
                    .map(it -> (GrantedAuthority) new SimpleGrantedAuthority(it))
                    .toList());
            if (isAuthenticated) newAuths.addAll(originalAuthentication.getAuthorities());
            Authentication token;
            if (isAuthenticated) {
                token = new RunAsUserToken(
                    "nekoshlink-runas-role-key",
                    originalAuthentication.getPrincipal(),
                    originalAuthentication.getCredentials(),
                    newAuths,
                    originalAuthentication.getClass()
                );
            } else {
                token = new AnonymousAuthenticationToken(
                    "nekoshlink-runas-role-key",
                    originalAuthentication == null ? "anonymous" : originalAuthentication.getPrincipal() == null ? "anonymous" : originalAuthentication.getPrincipal(),
                    newAuths
                );
            }
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        return this;
    }

    @Override
    public void close() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(originalAuthentication);
    }

    /**
     * Will run the code following this, until the end of the resource block, with the given roles.
     * If there is no authenticated principal at the moment of the call, this will not do anything and no
     * delegated privileges will be assigned, otherwise it will add the roles to the existing authenticated principal.
     */
    static public RunAs userWithRoles(String... roles) {
        Authentication orgAuth = SecurityContextHolder.getContext().getAuthentication();
        return new RunAs(orgAuth).prepare(false, roles);
    }

    /**
     * Will run the code following this, until the end of the resource block, with the given roles.
     * If there is no authenticated principal at the moment of the call, this will create a new anonymous
     * user with the given roles, otherwise it will add the roles to the existing authenticated principal.
     */
    static public RunAs anonymousWithRoles(String... roles) {
        Authentication orgAuth = SecurityContextHolder.getContext().getAuthentication();
        return new RunAs(orgAuth).prepare(true, roles);
    }

}
