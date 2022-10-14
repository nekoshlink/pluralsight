package org.nekosoft.shlink.cli.subcommands;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (
            auth == null
            || !auth.isAuthenticated()
            || !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Admin"))
        ) {
            throw new AccessDeniedException("Granted authority is not sufficient for this operation");
        }

        // Your command logic goes here

        return CommandLine.ExitCode.OK;

    }

}