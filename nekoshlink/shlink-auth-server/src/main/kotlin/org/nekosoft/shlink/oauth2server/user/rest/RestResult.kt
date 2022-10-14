package org.nekosoft.shlink.oauth2server.user.rest

data class RestResult<T>(
    var results: List<T>? = null,
    var pagination: PaginationData? = null,
)
