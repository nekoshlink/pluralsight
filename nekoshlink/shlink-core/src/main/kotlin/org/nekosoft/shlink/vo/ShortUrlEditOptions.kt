package org.nekosoft.shlink.vo

import picocli.CommandLine.Option

data class ShortUrlEditOptions(

    @field:Option(names = ["--id"])
    var id: Long? = null,

    @field:Option(names = ["-s", "--short-code"])
    var shortCode: String? = null,

    @field:Option(names = ["-d", "--domain"])
    var domain: String? = null,

)
