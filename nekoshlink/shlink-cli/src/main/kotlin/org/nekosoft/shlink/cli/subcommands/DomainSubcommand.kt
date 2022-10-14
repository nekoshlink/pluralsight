package org.nekosoft.shlink.cli.subcommands

import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.entity.Domain
import org.nekosoft.shlink.entity.Domain.Companion.DEFAULT_DOMAIN
import org.nekosoft.shlink.service.exception.NekoShlinkException
import org.nekosoft.shlink.vo.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import picocli.CommandLine

@CommandLine.Command(
    name = "domains",
    description = ["Manages the creation and maintenance of domains that can be be associated to Short Urls"],
    mixinStandardHelpOptions = true,
)
@Component
class DomainSubcommand(
    private val dao: DomainDataAccess
) {

    @CommandLine.Command(
        name = "create",
        description = ["Creates a new domain"],
        mixinStandardHelpOptions = true,
    )
    fun create(
        @CommandLine.Mixin meta: DomainCreateMeta,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_Admin"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        return try {
            val domain = dao.create(
                Domain(
                    authority = meta.authority,
                    scheme = meta.scheme,
                    baseUrlRedirect = meta.baseUrlRedirect,
                    regular404Redirect = meta.regular404Redirect,
                    invalidShortUrlRedirect = meta.invalidShortUrlRedirect,
                    isDefault = false,
                ))
            println(CommandLine.Help.Ansi.AUTO.string("Domain @|bold ${domain.id}|@ (${domain.scheme}://${domain.authority}${if (domain.isDefault) {" default"} else {""} }) created successfully"))
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

    @CommandLine.Command(
        name = "edit",
        description = ["Modifies an existing domain"],
        mixinStandardHelpOptions = true,
    )
    fun update(
        @CommandLine.Mixin meta: DomainEditMeta,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_Admin"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        return try {
            val domain = dao.update(
                Domain(
                    authority = meta.authority,
                    scheme = meta.scheme,
                    baseUrlRedirect = meta.baseUrlRedirect,
                    regular404Redirect = meta.regular404Redirect,
                    invalidShortUrlRedirect = meta.invalidShortUrlRedirect,
                ))
            println(CommandLine.Help.Ansi.AUTO.string("Domain @|bold ${domain.id}|@ (${domain.scheme}://${domain.authority}${if (domain.isDefault) {" default"} else {""} }) updated successfully"))
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

    @CommandLine.Command(
        name = "get",
        description = ["Gets information about an existing domain by authority"],
        mixinStandardHelpOptions = true,
    )
    fun get(
        @CommandLine.Parameters(index = "0") authority: String?,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_Admin"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        return try {
            val domain = dao.findByAuthority(authority)
            if (domain == null) {
                println(CommandLine.Help.Ansi.AUTO.string("The specified Domain does not exist..."))
            } else {
                println(CommandLine.Help.Ansi.AUTO.string("@|bold ${domain.id}|@ | ${domain.scheme}://${domain.authority}${if (domain.isDefault) {" (*)"} else {""} }"))
            }
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

    @CommandLine.Command(
        name = "list",
        description = ["Lists existing domains"],
        mixinStandardHelpOptions = true,
    )
    fun find(
        @CommandLine.Mixin options: DomainListOptions,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_Admin"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        return try {
            val domains = dao.list()
            if (domains.isEmpty) {
                println(CommandLine.Help.Ansi.AUTO.string("There are no Domains at the moment..."))
            } else {
                for (domain in domains) {
                    println(CommandLine.Help.Ansi.AUTO.string("@|bold ${domain.id}|@ | ${domain.scheme}://${domain.authority}${if (domain.isDefault) {" (*)"} else {""} }"))
                }
            }
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

    @CommandLine.Command(
        name = "default",
        description = ["Makes a domain the default one"],
        mixinStandardHelpOptions = true,
    )
    fun makeDefault(
        @CommandLine.Mixin meta: DomainDefaultMeta,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_Admin"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        return try {
            val domain = dao.makeDefault(meta.authority)
            println(CommandLine.Help.Ansi.AUTO.string("@|bold ${domain.id}|@ | ${domain.scheme}://${domain.authority}${if (domain.isDefault) {" (*)"} else {""} }"))
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

    @CommandLine.Command(
        name = "delete",
        description = ["Deletes an existing domain"],
        mixinStandardHelpOptions = true,
    )
    fun remove(
        @CommandLine.Parameters(index = "0", defaultValue = DEFAULT_DOMAIN) authority: String,
    ): Int {
        val auth = SecurityContextHolder.getContext().authentication
        if (
            auth == null
            || !auth.isAuthenticated
            || !auth.authorities.contains(SimpleGrantedAuthority("ROLE_Admin"))
        ) {
            throw AccessDeniedException("Granted authority is not sufficient for this operation")
        }
        return try {
            dao.remove(authority)
            println(CommandLine.Help.Ansi.AUTO.string("Domain $authority removed successfully"))
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

}