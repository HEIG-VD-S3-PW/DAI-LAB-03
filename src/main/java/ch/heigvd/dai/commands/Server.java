package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;

import ch.heigvd.dai.server.TCPServer;
import picocli.CommandLine;

@CommandLine.Command(
        name = "Server",
        description = "Start the server to accept remote connections",
        headerHeading = "\n=== Server Command ===\n\n",    // Adds a header for better separation
        synopsisHeading = "\nUsage: ",
        descriptionHeading = "\nDescription:\n",
        parameterListHeading = "\nArguments:\n",
        optionListHeading = "\nOptions:\n",
        footer = "\nCredits: Tristan Baud, Arno Tribolet and Mathieu Emery"
)
public class Server implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "1986")
    protected int port;

    @CommandLine.Option(
            names = {"-c", "--clients"},
            description = "Number of clients simultaneously connected (default: ${DEFAULT-VALUE}).",
            defaultValue = "10")
    protected int threads;

    @Override
    public Integer call() {
        try{
            TCPServer server = new TCPServer(port, threads);
            server.run();
        } catch(Exception e){
            System.err.println("Couldn't create a TCP connexion on the server side :" + e.getMessage());
        }

        return 1;
    }
}