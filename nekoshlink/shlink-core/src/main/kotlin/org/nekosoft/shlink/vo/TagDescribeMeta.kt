package org.nekosoft.shlink.vo;

import picocli.CommandLine.*

data class TagDescribeMeta(

    @field:Parameters(index = "0")
    var name: String,

    @field:Parameters(index = "1", arity = "0..1")
    var description: String?, // null means remove any existing description

) {
    // Needed for Picocli, otherwise it won't be able to create the class when instantiating command logic
    constructor() : this("", null)
}
