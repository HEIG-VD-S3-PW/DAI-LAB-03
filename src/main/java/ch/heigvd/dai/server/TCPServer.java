package ch.heigvd.dai.server;

// https://www.geeksforgeeks.org/multithreaded-servers-in-java/

import ch.heigvd.dai.protocol.CommandRegistry;

import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {

    private final int PORT;
    private final StreamingVideo streamingVideo;
    private final int NUMBER_OF_THREADS;

    public TCPServer(int port, int numberOfThreads) {
        this.PORT = port;
        this.NUMBER_OF_THREADS = numberOfThreads;
        this.streamingVideo = new StreamingVideo();
    }

    /**
     * Run the server
     */
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)) {

            System.out.println("Server listening for connections on port: " + PORT);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                executor.submit(new ClientHandler(clientSocket, streamingVideo));
            }
        } catch (IOException e) {
            System.out.println("[Server] exception: " + e);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final StreamingVideo streamingVideo;

        public ClientHandler(Socket socket, StreamingVideo streamingVideo) {
            this.clientSocket = socket;
            this.streamingVideo = streamingVideo;
        }

        public void run() {

            try (clientSocket;
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ){

                ServerCommandHandler protocolHandler = new ServerCommandHandler(in, out, this.streamingVideo);

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
