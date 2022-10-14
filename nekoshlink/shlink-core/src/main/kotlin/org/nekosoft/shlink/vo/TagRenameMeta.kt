package org.nekosoft.shlink.vo;

import picocli.CommandLine.*

data class TagRenameMeta(

    @field:Parameters(index = "0")
    var oldName: String,

    @field:Parameters(index = "1")
    var newName: String,

    @field:Option(names = ["--desc"], required = false)
    var newDescription: String?, // null means leave description unchanged

) {
    // Needed for Picocli, otherwise it won't be able to create the class when instantiating command logic
    constructor() : this("", "", null)
}
