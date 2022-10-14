package org.nekosoft.shlink.vo

import org.nekosoft.shlink.entity.Domain.Companion.DEFAULT_DOMAIN
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

data class ResolveMeta(

    @field:Parameters(index = "0")
    var shortCode: String? = null,

    @field:Option(names = ["-d", "--domain"], defaultValue = DEFAULT_DOMAIN)
    var domain: String = DEFAULT_DOMAIN,

    @field:Option(names = ["-p", "--password"], interactive = true, arity = "0..1", echo = false)
    var password: String? = null,

    @field:Option(names = ["--trail"])
    var pathTrail: String? = null,

    @field:Option(names = ["--query"])
    var queryParams: Map<String, Array<String>>? = null,

)
