package org.nekosoft.shlink.vo

import picocli.CommandLine.Option
import java.time.LocalDateTime

data class ShortUrlListOptions(

    @field:Option(names = ["--id"])
    var id: Long? = null,

    @field:Option(names = ["-s", "--short-code"])
    var shortCode: String? = null,

    @field:Option(names = ["-d", "--domain"])
    var domain: String? = null,

    @field:Option(names = ["--stats"], defaultValue = "false")
    var withStats: Boolean = false,

    @field:Option(names = ["-q", "--query-term"])
    var term: String? = null,

    @field:Option(names = ["--from"])
    var dateFrom: LocalDateTime? = null,

    @field:Option(names = ["--until"])
    var dateTo: LocalDateTime? = null,

    @field:Option(names = ["--tags"])
    var tags: List<String>? = null,

)
