package ch.heigvd.dai;

// https://www.geeksforgeeks.org/multithreaded-servers-in-java/

import java.io.*;
import java.net.InetSocketAddress;
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
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}