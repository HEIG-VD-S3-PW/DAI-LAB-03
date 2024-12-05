package ch.heigvd.dai.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import ch.heigvd.dai.client.TCPClient;
import picocli.CommandLine;

@CommandLine.Command(
        name = "Client",
        description = {
                "Start the client to connect to the streaming server.",
                "\nOnce connected the following commands are available:",
                "DELETE <id>   Delete a video that you select with it's number.",
                "LIST          Show all videos from the server",
                "WATCH <id>    Watch a specific video by using it's number",
                "UPLOAD        Upload a new video",
                "QUIT          Close the connection with the server"
        },
        footer = "\nCredits: Tristan Baud, Arno Tribolet and Mathieu Emery",
        headerHeading = "\n=== Client Command ===\n\n",    // Adds a header for better separation
        synopsisHeading = "\nUsage: ",
        descriptionHeading = "\nDescription:\n",
        parameterListHeading = "\nArguments:\n",
        optionListHeading = "\nOptions:\n")
public class Client implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-h", "--host"},
            description = "Host to connect to.",
            required = true)
    protected String host;

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "1986")
    protected int port;

    @Override
    public Integer call() {
        TCPClient client = new TCPClient(host, port);
        return 1;
    }
}