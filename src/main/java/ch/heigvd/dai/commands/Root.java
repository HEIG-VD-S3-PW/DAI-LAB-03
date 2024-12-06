package ch.heigvd.dai.commands;

import picocli.CommandLine;

@CommandLine.Command(
        mixinStandardHelpOptions = true,
        name = "Ammar",
        version = "1.0.0",
        description = "A command-line utility to stream videos from a remote server.",
        headerHeading = "\n======= Ammar Streaming Platform =======\n\n",  // Ensures header separation
        header = "Watch, add and remove videos from/to a remote server",
        footer = "\nCredits: Tristan Baud, Arno Tribolet and Mathieu Emery",
        synopsisHeading = "\nUsage: ",
        descriptionHeading = "\nDescription:\n",
        parameterListHeading = "\nArguments:\n",
        optionListHeading = "\nOptions:\n",
        commandListHeading = "\nCommands:\n",
        subcommands = {
                Client.class,  // Subcommand for the client
                Server.class   // Subcommand for server
        })
public class Root {}