package org.nekosoft.shlink.vo.rest

data class RestResult<T>(
    var results: List<T>? = null,
    var pagination: PaginationData? = null,
)
