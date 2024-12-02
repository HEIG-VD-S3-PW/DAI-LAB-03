package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;

import ch.heigvd.dai.server.TCPServer;
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
            TCPServer.main(new String[]{"-p", String.valueOf(port)});
        }
        catch(Exception e){
            System.err.println("Couldn't create a TCP connexion on the server side :" + e.getMessage());
        }

        return 1;
    }
}