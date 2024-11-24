package ch.heigvd.dai.client;

// https://www.geeksforgeeks.org/multithreaded-servers-in-java/

import ch.heigvd.dai.process.SignInClientProcess;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandRegistry;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    public TCPClient(String host, int port) throws IOException {

        try (Socket socket = new Socket(host, port);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            CommandRegistry registry = new CommandRegistry(in, out);

            SignInClientProcess signInClientProcess = new SignInClientProcess(in, out);
            signInClientProcess.execute();

            while(!socket.isClosed()) {

                System.out.print("> ");
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine().trim();

                if (input.equalsIgnoreCase("quit")) {
                    break;
                }

                String[] parts = input.split(" ", 2);
                String commandName = parts[0].toUpperCase();

                Command command = registry.getCommand(commandName);
                if (command == null) {
                    System.out.println("✗ Commande inconnue: " + commandName);
                    continue;
                }

                // Envoi au serveur
                out.write(input + "\n");
                out.flush();

                // Réception de la réponse
                command.receive();
            }

        }
        catch (Exception e) {
            System.out.println("Error in the connection: " + e.getMessage());
        }
    }
}