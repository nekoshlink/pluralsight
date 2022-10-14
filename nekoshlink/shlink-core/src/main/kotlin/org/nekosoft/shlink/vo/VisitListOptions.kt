package org.nekosoft.shlink.vo

import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.entity.support.VisitType
import picocli.CommandLine.Option
import java.time.LocalDateTime

data class VisitListOptions(

    @field:Option(names = ["-s", "--short-code"])
    var shortCode: String? = null,

    @field:Option(names = ["-d", "--domain"])
    var domain: String? = null,

    @field:Option(names = ["--from"])
    var dateFrom: LocalDateTime? = null,

    @field:Option(names = ["--until"])
    var dateTo: LocalDateTime? = null,

    @field:Option(names = ["--types"])
    var types: List<VisitType>? = null,

    @field:Option(names = ["--sources"])
    var sources: List<VisitSource>? = null,

    @field:Option(names = ["--tags"])
    var tags: List<String>? = null,

)
