package org.nekosoft.shlink.rest.controller.compat

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.nekosoft.shlink.vo.ShortUrlEditMeta
import org.nekosoft.shlink.vo.ShortUrlEditOptions
import java.time.LocalDateTime

class ShortUrlUpdateRequest {
    @JsonUnwrapped
    var meta: ShortUrlEditMeta? = null

    @JsonUnwrapped
    var options: ShortUrlEditOptions? = null

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd['T'HH:mm[:ss][ZZZZZ]]")
    var validSince: LocalDateTime? = null

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd['T'HH:mm[:ss][ZZZZZ]]")
    var validUntil: LocalDateTime? = null
}