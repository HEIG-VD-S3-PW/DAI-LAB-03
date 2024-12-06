package ch.heigvd.dai.client;

import ch.heigvd.dai.process.Process;
import ch.heigvd.dai.process.SignInClientProcess;
import ch.heigvd.dai.process.UploadProcess;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandRegistry;
import ch.heigvd.dai.protocol.commands.QuitCommand;
import ch.heigvd.dai.utils.Utils;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    private final String HOST;
    private final int PORT;

    public TCPClient(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    public void run(){
        try (
                Socket socket = new Socket(HOST, PORT);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {
            CommandRegistry registry = new CommandRegistry(in, out);

            System.out.print("COMMANDS AVAILABLE: ");
            registry.getCommands().forEach((k, v) -> System.out.print(k + " "));
            System.out.println();

            while (!socket.isClosed()) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) continue;

                if (handleSpecialCommand(input, in, out, registry)) continue;

                Command command = registry.getCommand(input.split(" ")[0].toUpperCase());
                if (command == null) {
                    System.out.println("Unknown command: " + input);
                    continue;
                }

                Utils.send(out, input);
                command.receive();

                if (command instanceof QuitCommand) break;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handle special commands on the client side
     *
     * @param input    The input string
     * @param in       The input stream
     * @param out      The output stream
     * @param registry The command registry
     * @return True if the command was handled, false otherwise
     */
    private boolean handleSpecialCommand(String input, BufferedReader in, BufferedWriter out, CommandRegistry registry) {
        try {
            if (input.equalsIgnoreCase("connect")) {
                executeProcess(new SignInClientProcess(in, out), "CONNECT", registry);
                return true;
            }
            if (input.toLowerCase().startsWith("upload")) {
                executeProcess(new UploadProcess(in, out), "UPLOAD", registry);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return true;
        }
    }

    /**
     * Execute a process and receive the command
     *
     * @param process
     * @param commandName
     * @param registry
     * @throws Exception
     */
    private void executeProcess(Process process, String commandName, CommandRegistry registry) throws Exception {
        if (!process.execute()) return;
        Command command = registry.getCommand(commandName);
        if (command != null) command.receive();
    }
}