package org.nekosoft.shlink.cli.subcommands

import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.service.ShortUrlManager
import org.nekosoft.shlink.service.VisitDataEnricher
import org.nekosoft.shlink.service.exception.NekoShlinkException
import org.nekosoft.shlink.vo.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import picocli.CommandLine.ExitCode
import picocli.CommandLine.Command
import picocli.CommandLine.Help.Ansi
import picocli.CommandLine.Mixin
import java.io.File

@Command(
    name = "short-urls",
    description = ["Manages the creation and maintenance of Short URL definitions"],
    mixinStandardHelpOptions = true,
)
@Component
class ShortUrlSubcommand(
    private val shortUrls: ShortUrlManager
) {

    @Command(
        name = "create",
        description = ["Creates a new Short URL"],
        mixinStandardHelpOptions = true,
    )
    fun create(
        @Mixin meta: ShortUrlCreateMeta,
        @Mixin options: ShortUrlCreateOptions,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_User"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        return try {
            val shortUrl = shortUrls.create(meta, options)
            println(Ansi.AUTO.string("Short URL @|bold ${shortUrl.id}|@ (${shortUrl.domain.authority} / ${shortUrl.shortCode}) for ${shortUrl.longUrl} created successfully"))
            ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            ExitCode.USAGE
        }
    }

    @Command(
        name = "edit",
        description = ["Modifies an existing Short URL"],
        mixinStandardHelpOptions = true,
    )
    fun edit(
        @Mixin meta: ShortUrlEditMeta,
        @Mixin options: ShortUrlEditOptions,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_User"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        return try {
            val shortUrl = shortUrls.edit(meta, options)
            println(Ansi.AUTO.string("Short URL @|bold ${shortUrl.id}|@ (${shortUrl.domain.authority} / ${shortUrl.shortCode}) for ${shortUrl.longUrl} updated successfully"))
            ExitCode.OK
        } catch (e: NullPointerException) {
            System.err.println("Missing required parameters - the edit command requires that you submit the FULL state of the Short URL")
            ExitCode.USAGE
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            ExitCode.USAGE
        }
    }

    @Command(
        name = "delete",
        description = ["Deletes an existing Short URL"],
        mixinStandardHelpOptions = true,
    )
    fun delete(
        @Mixin options: ShortUrlEditOptions,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_User"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        return try {
            shortUrls.delete(options)
            println(Ansi.AUTO.string("Short URL @|bold ${options.id}|@ (${options.domain} / ${options.shortCode}) deleted successfully"))
            ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            ExitCode.USAGE
        }
    }

    @Command(
        name = "list",
        description = ["Shows a list of the Short URLs in the system"],
        mixinStandardHelpOptions = true,
    )
    fun list(
        @Mixin options: ShortUrlListOptions,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_User"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        val results = shortUrls.listWithStats(options).content
        if (results.isEmpty()) {
            println(Ansi.AUTO.string("There are no Short URLs at the moment..."))
        } else {
            for (shortUrl in results) {
                print(Ansi.AUTO.string("@|bold ${shortUrl.shortUrl.id}|@ | ${shortUrl.shortUrl.domain.authority} / ${shortUrl.shortUrl.shortCode} | ${shortUrl.shortUrl.longUrl} | Pwd: ${shortUrl.shortUrl.password ?: "<none>"}"))
                if (options.withStats) {
                    print(Ansi.AUTO.string("  (tot: @|bold ${shortUrl.visitCount}|@ ・ err: @|bold ${shortUrl.errorVisitCount}|@ ・ fb: @|bold ${shortUrl.forbiddenVisitCount}|@ ・ oot: @|bold ${shortUrl.outOfTimeVisitCount}|@ ・ ol: @|bold ${shortUrl.overLimitVisitCount}|@)"))
                }
                println()
            }
        }
        return ExitCode.OK
    }

    @Command(
        name = "resolve",
        description = ["Resolves a Short URL into its corresponding Long URL"],
        mixinStandardHelpOptions = true,
    )
    fun resolve(
        @Mixin meta: ResolveMeta,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_Anyone"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        val enricher: VisitDataEnricher = {
            Visit(
                source = VisitSource.CLI,
                pathTrail = meta.pathTrail,
            )
        }
        return try {
            val (shortUrl, _) = shortUrls.resolve(meta, enricher)
            if (shortUrl == null) {
                println(Ansi.AUTO.string("The given Short URL does not exist"))
            } else {
                println(Ansi.AUTO.string("${shortUrl.domain.authority} / ${shortUrl.shortCode} -> ${shortUrl.longUrl}"))
            }
            ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            ExitCode.USAGE
        }
    }

    @Command(
        name = "qr-resolve",
        description = ["Resolves a Short URL into its corresponding QR Code and writes the QR Code to disk"],
        mixinStandardHelpOptions = true,
    )
    fun qrResolve(
        @Mixin meta: ResolveMeta,
        @Mixin options: QRCodeOptions,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_Anyone"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        val enricher: VisitDataEnricher = {
            Visit(
                source = VisitSource.CLI_QR,
                pathTrail = meta.pathTrail,
            )
        }
        return try {
            val bytes = shortUrls.qrResolve(meta, options, enricher)
            if (bytes == null) {
                println(Ansi.AUTO.string("The given Short URL does not exist"))
            } else {
                File(options.filename).writeBytes(bytes.toByteArray())
                println(Ansi.AUTO.string("QR Code for ${meta.domain} / ${meta.shortCode} written to disk as ${options.filename}"))
            }
            ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            ExitCode.USAGE
        }
    }

}
