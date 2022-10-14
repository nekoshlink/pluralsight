package org.nekosoft.shlink.entity.support

enum class VisitType {
    SUCCESSFUL,
    INVALID_REQUEST,
    FORBIDDEN,
    EXPIRED,
    DISABLED,
    OVER_LIMIT,
    PATH_TRAIL_ERROR,
    QUERY_PARAMS_ERROR,
    ORPHAN,
}
