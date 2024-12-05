package ch.heigvd.dai.client;

import ch.heigvd.dai.process.Process;
import ch.heigvd.dai.process.SignInClientProcess;
import ch.heigvd.dai.process.UploadProcess;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandRegistry;
import ch.heigvd.dai.protocol.commands.QuitCommand;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
    public TCPClient(String host, int port) {
        try (
                Socket socket = new Socket(host, port);
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

                out.write(input + "\n");
                out.flush();
                command.receive();

                if (command instanceof QuitCommand) break;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private boolean handleSpecialCommand(String input, BufferedReader in, BufferedWriter out, CommandRegistry registry) {
        try {
            if (input.equalsIgnoreCase("connect")) {
                return executeProcess(new SignInClientProcess(in, out), "CONNECT", registry);
            }
            if (input.toLowerCase().startsWith("upload")) {
                return executeProcess(new UploadProcess(in, out), "UPLOAD", registry);
            }
            return false;
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            return true;
        }
    }

    private boolean executeProcess(Process process, String commandName, CommandRegistry registry) throws Exception {
        if (!process.execute()) return true;
        Command command = registry.getCommand(commandName);
        if (command != null) command.receive();
        return true;
    }
}