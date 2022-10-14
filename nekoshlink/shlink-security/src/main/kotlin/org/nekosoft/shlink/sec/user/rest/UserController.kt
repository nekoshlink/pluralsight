package org.nekosoft.shlink.sec.user.rest

import org.nekosoft.shlink.sec.ShlinkSecurityConfiguration.VERSION_STRING
import org.nekosoft.shlink.sec.user.User
import org.nekosoft.shlink.sec.user.UserDataAccess
import org.nekosoft.shlink.sec.user.rest.PaginationData.Companion.paginationToPageable
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("adm/v$VERSION_STRING/users") // this short mapping guarantees it cannot be a short code (min five characters)
@ConditionalOnWebApplication
class UserController(
    private val users: UserDataAccess,
) {

    @GetMapping
    fun users(options: UserListOptions, pagination: PaginationOptions): ResponseEntity<RestResult<User>> {
        val results = users.loadAllUsers(options, paginationToPageable(pagination))
        return ResponseEntity.status(HttpStatus.OK).body(RestResult(
            results = results.content.map { it.user },
            pagination = PaginationData.fromPage(results)
        ))
    }

    @PostMapping()
    fun addUser(@RequestBody user: User): ResponseEntity<User> {
        val newUser = users.createUser(user)
        return ResponseEntity.status(HttpStatus.OK).body(newUser)
    }

    @PatchMapping("{id}")
    fun editUser(@RequestBody user: User, @PathVariable("id") userId: Long): ResponseEntity<User> {
        user.id = userId
        user.lastModifiedDate = LocalDateTime.now()
        val newUser = users.updateUser(user)
        return ResponseEntity.status(HttpStatus.OK).body(newUser)
    }

    @PatchMapping("password")
    fun changePassword(@RequestBody meta: ChangePasswordMeta): ResponseEntity<Void> {
        users.changePassword(meta.username, meta.oldPassword, meta.newPassword)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @PatchMapping("api-key")
    fun addApiKey(@RequestBody meta: ChangeApiKeyMeta): ResponseEntity<Void> {
        users.addApiKey(meta.username, meta.apiKey)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @DeleteMapping("api-key")
    fun deleteApiKey(@RequestBody meta: ChangeApiKeyMeta): ResponseEntity<Void> {
        if (meta.apiKey != null) {
            val user = users.loadUserByApiKey(meta.apiKey!!)
            users.removeApiKey(user.username)
        } else {
            users.removeApiKey(meta.username)
        }
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

}
