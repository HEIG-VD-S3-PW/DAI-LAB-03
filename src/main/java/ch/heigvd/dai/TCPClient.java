package ch.heigvd.dai;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
    private int port;
    private Socket socket;

    public TCPClient(String host, int port) throws IOException {
        this.port = port;
        socket = new Socket(host, port);

        // Setup output stream to send data to the server
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Setup input stream to receive data from the server
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Receive response from the server
        String response = in.readLine();
        System.out.println(response);

        Scanner scanner = new Scanner(System.in);

        do{
            System.out.print("Enter your pseudo: ");
            String pseudo = scanner.nextLine();
            out.println(pseudo);
            response = in.readLine();
        }while(response.equals("Invalid entry."));


        // Prompt the user to enter their email
        do{
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            out.println(email);
            response = in.readLine();
        }while(response.equals("Invalid entry."));

        // Get all videos
        while(true) {
            response = in.readLine();
            if(response.equals("end")) break;
            System.out.println(response);
        }

        do{
            System.out.print("Enter your choice: ");
            String videoChoice = scanner.nextLine();
            out.println(videoChoice);
            response = in.readLine();
        }while(response.equals("Invalid entry."));

        // Close the socket
        socket.close();
    }
}