package org.nekosoft.shlink.oauth2server.user.rest

data class ChangePasswordMeta(
    var username: String,
    var oldPassword: String,
    var newPassword: String,
)