package org.nekosoft.shlink.vo

import picocli.CommandLine.Option

data class TagListOptions(

    @field:Option(names = ["--id"])
    var id: Long? = null,

    @field:Option(names = ["-n", "--name"])
    var name: String? = null,

    @field:Option(names = ["-p", "--name-pattern"])
    var namePattern: String? = null,

    @field:Option(names = ["--stats"], defaultValue = "false", negatable = true, arity = "0")
    var withStats: Boolean = false,

)
