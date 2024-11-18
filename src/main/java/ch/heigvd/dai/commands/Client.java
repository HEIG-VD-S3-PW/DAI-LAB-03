package ch.heigvd.dai.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import ch.heigvd.dai.TCPClient;
import picocli.CommandLine;

@CommandLine.Command(name = "client", description = "Start the client part.")
public class Client implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-H", "--host"},
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
        try{
            TCPClient client = new TCPClient(host, port);
        }
        catch(IOException e){
            System.err.println("Couldn't create a TCP connexion on the client side :" + e.getMessage());
        }
        return 1;
    }
}