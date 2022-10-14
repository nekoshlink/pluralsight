package org.nekosoft.shlink.vo;

import org.nekosoft.shlink.entity.Domain.Companion.DEFAULT_DOMAIN
import picocli.CommandLine

data class DomainDefaultMeta(

    @field:CommandLine.Parameters(index = "0", defaultValue = DEFAULT_DOMAIN)
    var authority: String = DEFAULT_DOMAIN,

)
