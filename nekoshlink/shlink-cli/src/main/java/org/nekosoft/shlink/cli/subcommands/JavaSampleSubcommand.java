package org.nekosoft.shlink.cli.subcommands;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@CommandLine.Command(
    name = "example",
    description = "Show authorization code for the CLI in Java",
    mixinStandardHelpOptions = true
)
@Component
public class JavaSampleSubcommand {

    @CommandLine.Command(
        name = "java",
        description = "Java code for authorizing command",
        mixinStandardHelpOptions = true
    )
    public int javaCommand() {

        // Your command logic goes here

        return CommandLine.ExitCode.OK;

    }

}