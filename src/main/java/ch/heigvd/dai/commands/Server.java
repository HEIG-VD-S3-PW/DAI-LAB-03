package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;

import ch.heigvd.dai.StreamingVideo;
import ch.heigvd.dai.TCPServer;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.User;
import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start the server part.")
public class Server implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "1986")
    protected int port;

    @Override
    public Integer call() {
        try{
            TCPServer server = new TCPServer(port);
        }
        catch(Exception e){
            System.err.println("Couldn't create a TCP connexion on the server side :" + e.getMessage());
        }

        return 1;
    }
}