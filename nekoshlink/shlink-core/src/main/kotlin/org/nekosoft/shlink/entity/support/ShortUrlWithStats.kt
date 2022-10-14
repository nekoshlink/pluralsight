package org.nekosoft.shlink.entity.support

import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.nekosoft.shlink.entity.ShortUrl

data class ShortUrlWithStats(

    @JsonUnwrapped
    var shortUrl: ShortUrl,

    val visitCount: Long,

    val successVisitCount: Long,

    val forbiddenVisitCount: Long,

    val outOfTimeVisitCount: Long,

    val overLimitVisitCount: Long,

    val errorVisitCount: Long,

)
