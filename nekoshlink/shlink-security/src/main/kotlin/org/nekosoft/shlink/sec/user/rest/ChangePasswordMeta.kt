package org.nekosoft.shlink.sec.user.rest

data class ChangePasswordMeta(
    var username: String,
    var oldPassword: String,
    var newPassword: String,
)