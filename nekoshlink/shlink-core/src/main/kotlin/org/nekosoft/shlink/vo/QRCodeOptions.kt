package org.nekosoft.shlink.vo

import picocli.CommandLine.Option

data class QRCodeOptions(
    @field:Option(names = ["--filename"], defaultValue = "nekoshlink.png")
    var filename: String = "nekoshlink.png",

    @field:Option(names = ["--size"], defaultValue = "256")
    var size: Int = 256
) {

    companion object {
        operator fun invoke(
            filename: String? = null,
            size: Int? = null,
        ) = QRCodeOptions(
            filename ?: "nekoshlink.png",
            size ?: 256
        )
    }

}
