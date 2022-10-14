package org.nekosoft.shlink.cli

import org.nekosoft.shlink.cli.subcommands.*
import picocli.CommandLine
import java.io.File

@CommandLine.Command(
    name = "nkshlink",
    mixinStandardHelpOptions = true,
    version = ["neko-shlink 1.0.0"],
    description = ["A command line interface for NekoShlink, a URL shortener written in Kotlin with Spring Boot"],
    subcommands = [
        ShortUrlSubcommand::class,
        TagSubcommand::class,
        DomainSubcommand::class,
        VisitSubcommand::class,
    ]
)
class ShlinkCommand {

    @field:CommandLine.Option(names = ["--access-token-file"])
    var accessToken: File? = null

    @field:CommandLine.Option(names = ["--usr"], interactive = true, arity = "0..1", echo = true, description = ["The username to log in as. If the argument is empty, it can be entered interactively."])
    var authUser: String? = null

    @field:CommandLine.Option(names = ["--pwd"], interactive = true, arity = "0..1", echo = false, description = ["The password for the given username. If the argument is empty, it can be entered interactively, but typed text will not show on the screen."])
    var authPassword: String? = null

    @field:CommandLine.Option(names = ["--api-key"], interactive = true, arity = "0..1", echo = false, description = ["The API Key to use for submitting API requests. Use either this or a username with password."])
    var apiKey: String? = null

}
