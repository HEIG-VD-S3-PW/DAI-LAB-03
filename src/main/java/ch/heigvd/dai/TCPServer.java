package ch.heigvd.dai;

// https://medium.com/@gaurangjotwani/creating-a-tcp-connection-between-two-servers-in-java-27fabe53deaa

import java.net.*;
import java.io.IOException;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        // Here, we create a Socket instance named socket
        ServerSocket serverSocket = new ServerSocket(1986);
        System.out.println("Listening for clients...");
        Socket clientSocket = serverSocket.accept();
        String clientSocketIP = clientSocket.getInetAddress().toString();
        int clientSocketPort = clientSocket.getPort();
        System.out.println("[IP: " + clientSocketIP + " ,Port: " + clientSocketPort +"]  " + "Client Connection Successful!");
    }
}