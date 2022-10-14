package org.nekosoft.shlink.vo

import picocli.CommandLine.Option

data class DomainListOptions(

    @field:Option(names = ["--id"])
    var id: Long? = null,

    @field:Option(names = ["-a", "--authority"])
    var host: String? = null,

    @field:Option(names = ["-p", "--host-pattern"])
    var hostPattern: String? = null,

    @field:Option(names = ["-s", "--scheme"])
    var scheme: String? = null,

)
