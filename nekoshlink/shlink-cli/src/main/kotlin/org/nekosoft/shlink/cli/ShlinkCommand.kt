package org.nekosoft.shlink.cli

import org.nekosoft.shlink.cli.subcommands.*
import picocli.CommandLine

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
class ShlinkCommand