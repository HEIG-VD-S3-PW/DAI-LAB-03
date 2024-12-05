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
        try (ServerSocket serverSocket = new ServerSocket(port); ) {
            while (!serverSocket.isClosed()) {
                System.out.println("Server listening for connections on port: " + port);
                Socket clientSocket = serverSocket.accept();
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

                CommandRegistry registry = new CommandRegistry(in, out);
                ServerCommandHandler protocolHandler = new ServerCommandHandler(registry, in, out, null, streamingVideo);

                while(!clientSocket.isClosed()){

                    String line = in.readLine();

                    if(line == null || line.isEmpty()) {
                        continue;
                    }

                    System.out.println("Command received: " + line);
                    protocolHandler.handleLine(line);

                }

            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
