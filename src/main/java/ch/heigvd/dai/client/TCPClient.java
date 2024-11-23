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
        // establish a connection by providing host and port
        // number
        try (Socket socket = new Socket(host, port)) {

            // writing to server
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);

            // reading from server
            BufferedReader in
                    = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            CommandRegistry registry = new CommandRegistry(in, out);


            SignInClientProcess signInClientProcess = new SignInClientProcess(in, out);
            signInClientProcess.execute();


            /*
            do{
                System.out.print("Enter your choice: ");
                String videoChoice = scanner.nextLine();
                out.println(videoChoice);
                response = in.readLine();
            }while(response.equals("Invalid entry."));
*/
            while(true){
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
                out.println(input);
                out.flush();

                // Affichage de la réponse
                try {
                    String resp = in.readLine();
                    System.out.println(resp);
                } catch (IOException e) {
                    System.out.println("✗ Erreur de communication: " + e.getMessage());
                }
            }

            // Close the socket
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}