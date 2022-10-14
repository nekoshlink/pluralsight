package org.nekosoft.shlink.vo

import org.nekosoft.shlink.entity.ShortUrl.Companion.MIN_LENGTH
import picocli.CommandLine.Option

data class ShortUrlCreateOptions(

    @field:Option(names = ["--reuse"])
    var findIfExists: Boolean = false,

    @field:Option(names = ["--validate"])
    var validateUrl: Boolean = false,

    @field:Option(names = ["--use-title"], defaultValue = true.toString())
    var useValidatedTitle: Boolean = true,

    @field:Option(names = ["--length"], defaultValue = MIN_LENGTH.toString())
    var shortCodeLength: Int = MIN_LENGTH,

)
