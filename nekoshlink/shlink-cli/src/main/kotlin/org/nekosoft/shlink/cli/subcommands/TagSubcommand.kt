package org.nekosoft.shlink.cli.subcommands

import org.nekosoft.shlink.dao.TagDataAccess
import org.nekosoft.shlink.service.exception.NekoShlinkException
import org.nekosoft.shlink.vo.TagDescribeMeta
import org.nekosoft.shlink.vo.TagListOptions
import org.nekosoft.shlink.vo.TagRenameMeta
import org.springframework.stereotype.Component
import picocli.CommandLine

@CommandLine.Command(
    name = "tags",
    description = ["Manages the creation and maintenance of tags that can be assigned to Short Urls"],
    mixinStandardHelpOptions = true,
)
@Component
class TagSubcommand(
    private val dao: TagDataAccess
) {

    @CommandLine.Command(
        name = "create",
        description = ["Creates a new tag"],
        mixinStandardHelpOptions = true,
    )
    fun create(
        @CommandLine.Parameters(index = "0") name: String,
        @CommandLine.Option(names = ["--desc", "--description"], required = false) description: String? = null,
    ): Int {
        return try {
            val tag = dao.create(name, description)
            println(CommandLine.Help.Ansi.AUTO.string("Tag @|bold ${tag.id}|@ (${tag.name}${tag.description?.let { " : $it" } ?: ""}) created successfully"))
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

    @CommandLine.Command(
        name = "get",
        description = ["Gets information about an existing tag by name"],
        mixinStandardHelpOptions = true,
    )
    fun get(
        @CommandLine.Parameters(index = "0") name: String,
    ): Int {
        return try {
            val tag = dao.findByName(name)
            if (tag == null) {
                println(CommandLine.Help.Ansi.AUTO.string("The specified Tag Name does not exist..."))
            } else {
                println(CommandLine.Help.Ansi.AUTO.string("@|bold ${tag.id}|@ | ${tag.name} | ${tag.description ?: "-"}"))
            }
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

    @CommandLine.Command(
        name = "list",
        description = ["Lists or finds existing tags"],
        mixinStandardHelpOptions = true,
    )
    fun find(
        @CommandLine.Mixin options: TagListOptions,
    ): Int {
        return try {
            val tags = dao.findAll(options)
            if (tags.isEmpty) {
                println(CommandLine.Help.Ansi.AUTO.string("There are no Tags at the moment..."))
            } else {
                for (tag in tags) {
                    println(CommandLine.Help.Ansi.AUTO.string("@|bold ${tag.id}|@ | ${tag.name} | ${tag.description ?: "-"} | S:${tag.shortUrlCount} | V:${tag.visitCount}"))
                }
            }
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

    @CommandLine.Command(
        name = "rename",
        description = ["Renames an existing tag, optionally providing a description. If no description is given, no changes will be made to the existing description."],
        mixinStandardHelpOptions = true,
    )
    fun rename(
        @CommandLine.Mixin options: TagRenameMeta,
    ): Int {
        return try {
            val tag = dao.rename(options.oldName, options.newName, options.newDescription)
            println(CommandLine.Help.Ansi.AUTO.string("Tag @|bold ${tag.id}|@ (${tag.name}${tag.description?.let { " : $it" } ?: ""}) renamed successfully"))
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

    @CommandLine.Command(
        name = "describe",
        description = ["Adds a description to an existing tag. If no description is given, the existing description will be removed."],
        mixinStandardHelpOptions = true,
    )
    fun describe(
        @CommandLine.Mixin options: TagDescribeMeta,
    ): Int {
        return try {
            val tag = dao.describe(options.name, options.description)
            println(CommandLine.Help.Ansi.AUTO.string("Tag @|bold ${tag.id}|@ (${tag.name}${tag.description?.let { " : $it" } ?: ""}) described successfully"))
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

    @CommandLine.Command(
        name = "delete",
        description = ["Removes an existing tag. It will remove the tag from any Short Url that contain it."],
        mixinStandardHelpOptions = true,
    )
    fun delete(
        @CommandLine.Parameters(index = "0") name: String,
    ): Int {
        return try {
            dao.deleteByName(name)
            println(CommandLine.Help.Ansi.AUTO.string("Tag @|bold ${name}|@ deleted successfully"))
            CommandLine.ExitCode.OK
        } catch (e: NekoShlinkException) {
            System.err.println(e.message)
            CommandLine.ExitCode.USAGE
        }
    }

}