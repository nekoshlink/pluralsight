package org.nekosoft.shlink.vo;

import picocli.CommandLine

data class TagCreateMeta(

    @CommandLine.Parameters(index = "0")
    var name: String,

    @CommandLine.Option(names = ["--desc"], required = false)
    var description: String?, // null means no description will be given

)
