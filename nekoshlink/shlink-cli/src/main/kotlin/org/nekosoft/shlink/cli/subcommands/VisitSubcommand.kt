package org.nekosoft.shlink.cli.subcommands

import org.nekosoft.shlink.dao.VisitDataAccess
import org.nekosoft.shlink.service.exception.NekoShlinkException
import org.nekosoft.shlink.vo.*
import org.springframework.stereotype.Component
import picocli.CommandLine

@CommandLine.Command(
    name = "visits",
    description = ["Provides access to the record of Short Url visits"],
    mixinStandardHelpOptions = true,
)
@Component
class VisitSubcommand(
    private val dao: VisitDataAccess
) {

    @CommandLine.Command(
        name = "list",
        description = ["List all visits with optional filters"],
        mixinStandardHelpOptions = true,
    )
    fun list(
        @CommandLine.Mixin options: VisitListOptions,
    ): Int {
        return try {
            val visits = dao.getVisits(options)
            if (visits.isEmpty) {
                println(CommandLine.Help.Ansi.AUTO.string("There are no Visits at the moment..."))
            } else {
                for (visit in visits) {
                    println(CommandLine.Help.Ansi.AUTO.string("@|bold ${visit.id}|@ | ${visit.date} | ${visit.domain} | ${visit.shortCode} | ${visit.source} | ${visit.type} | ${visit.referrer} "))
                }
            }
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

}