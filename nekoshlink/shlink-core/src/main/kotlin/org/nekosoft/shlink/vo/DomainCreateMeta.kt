package org.nekosoft.shlink.vo;

import org.nekosoft.shlink.entity.Domain
import picocli.CommandLine

data class DomainCreateMeta(

    @field:CommandLine.Parameters(index = "0")
    var authority: String = Domain.DEFAULT_DOMAIN,

    @field:CommandLine.Option(names = ["--scheme"], defaultValue = "https")
    var scheme: String = "https",

    // for compatibility with Shlink.io
    @field:CommandLine.Option(names = ["--base"])
    var baseUrlRedirect: String? = null,

    // for compatibility with Shlink.io
    @field:CommandLine.Option(names = ["--404"])
    var regular404Redirect: String? = null,

    // for compatibility with Shlink.io
    @field:CommandLine.Option(names = ["--invalid"])
    var invalidShortUrlRedirect: String? = null,

)
