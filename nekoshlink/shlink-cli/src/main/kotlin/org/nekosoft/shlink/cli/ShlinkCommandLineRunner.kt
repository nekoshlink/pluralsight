package org.nekosoft.shlink.cli

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.stereotype.Component
import picocli.CommandLine
import picocli.CommandLine.IExecutionStrategy
import picocli.CommandLine.IFactory
import picocli.CommandLine.PicocliException
import java.util.*
import kotlin.system.exitProcess

@Component
class ShlinkCommandLineRunner(
    val factory: IFactory,
    // pluggable execution strategy (will be created in security module)
    val executionStrategy: Optional<IExecutionStrategy>,
) : CommandLineRunner, ExitCodeGenerator {

    private var exitCode = 0

    override fun run(vararg args: String?) {
        exitCode = try {
            exitProcess(
                CommandLine(ShlinkCommand(), factory)
                    .setExecutionStrategy {
                        // Is there an execution strategy?
                        if (executionStrategy.isPresent) {
                            // Yes, I'm executing it
                            val result = executionStrategy.get().execute(it)
                            // Has it returned successfully?
                            if (result != 0) {
                                // No, I'm aborting with result code
                                return@setExecutionStrategy result
                            }
                        }
                        // All ok so far - about to execute RunLast::execute
                        CommandLine.RunLast().execute(it)
                    }
                    .setExecutionExceptionHandler {
                        ex, cmd, _ -> cmd.err.println(ex.message); -1
                    }
                    .setUsageHelpAutoWidth(true)
                    .execute(*args)
            )
        } catch (e: PicocliException) {
            System.err.println(e.message)
            -1
        } catch (e: Exception) {
            System.err.println("A system error has occurred!")
            System.err.println("${e.javaClass.name}: ${e.message}")
            -9
        }
    }

    override fun getExitCode(): Int {
        return exitCode
    }

}
