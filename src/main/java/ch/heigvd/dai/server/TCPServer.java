package ch.heigvd.dai.server;

// https://www.geeksforgeeks.org/multithreaded-servers-in-java/

import ch.heigvd.dai.protocol.CommandRegistry;

import java.net.*;
import java.io.*;

public class TCPServer {
    private static final int port = 1986;
    private static final StreamingVideo streamingVideo = new StreamingVideo();
    private static final int NUMBER_OF_THREADS = 10;

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server listening for connections on port: " + port);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("[Server] exception: " + e);
        }
    }

    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        public void run()
        {

            try (clientSocket;
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ){

                ServerCommandHandler protocolHandler = new ServerCommandHandler(in, out, streamingVideo);

                while(!clientSocket.isClosed()){

                    String line = in.readLine();

                    if(line == null || line.isEmpty()) {
                        continue;
                    }

                    if(protocolHandler.handleLine(line)){
                        clientSocket.close();
                    }

                }

            }
            catch (Exception e) {
                System.out.println("[ClientHandler] exception: " + e);
            }
        }
    }

}
