package org.nekosoft.shlink.sec.user.rest

data class RestResult<T>(
    var results: List<T>? = null,
    var pagination: PaginationData? = null,
)
