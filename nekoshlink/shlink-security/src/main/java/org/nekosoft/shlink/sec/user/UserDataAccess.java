package org.nekosoft.shlink.sec.user;

import org.nekosoft.shlink.sec.user.rest.UserListOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/*
 * Our data access implements the UserDetailsService interface for interoperability with Spring Security, but it is
 * effectively a DAO object subject to authorization constraints
 */
public interface UserDataAccess extends UserDetailsService {

    @Override
    NekoShlinkUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    NekoShlinkUserDetails loadUserByApiKey(String apiKey);

    /**
     * Get a list of all the users
     */
    Page<NekoShlinkUserDetails> loadAllUsers(UserListOptions options, Pageable pageable);

    /**
     * Create a new user with the supplied details.
     */
    User createUser(User user);

    /**
     * Update the specified user.
     */
    User updateUser(User user);

    /**
     * Remove the user with the given login name from the system.
     */
    void deleteUser(String username);

    /**
     * Modify the current user's password. This should change the user's password in the
     * persistent user repository (datbase, LDAP etc).
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    void changePassword(String username, String oldPassword, String newPassword);

    /**
     * Add a legacy API key to the user
     */
    void addApiKey(String username, String key);

    /**
     * Remove the legacy API key from the user
     */
    void removeApiKey(String username);

    /**
     * Check if a user with the supplied login name exists in the system.
     */
    boolean userExists(String username);

}
