package org.nekosoft.shlink.rest.controller.compat

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.nekosoft.shlink.vo.ShortUrlCreateMeta
import org.nekosoft.shlink.vo.ShortUrlCreateOptions
import java.time.LocalDateTime

class ShortUrlCreateRequest{
    @field:JsonUnwrapped
    var meta: ShortUrlCreateMeta? = null

    @field:JsonUnwrapped
    var options: ShortUrlCreateOptions? = null

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd['T'HH:mm[:ss][ZZZZZ]]")
    var validSince: LocalDateTime? = null

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd['T'HH:mm[:ss][ZZZZZ]]")
    var validUntil: LocalDateTime? = null
}