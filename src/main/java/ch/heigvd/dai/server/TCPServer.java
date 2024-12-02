package ch.heigvd.dai.server;

// https://www.geeksforgeeks.org/multithreaded-servers-in-java/

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.process.SignInServerProcess;
import ch.heigvd.dai.protocol.CommandRegistry;

import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

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

                out.write("Welcome to the Amar Streaming Platform !" + "\n");
                out.flush();

                SignInServerProcess signInServerProcess = new SignInServerProcess(in, out, streamingVideo);
                signInServerProcess.execute();

                if(signInServerProcess.getUser() == null){
                    return;
                }
                User user = signInServerProcess.getUser();

                CommandRegistry registry = new CommandRegistry(in, out);
                ServerCommandHandler protocolHandler = new ServerCommandHandler(registry, in, out, user, streamingVideo);

                while(!clientSocket.isClosed()){

                    String line = in.readLine();

                    if(line == null || line.isEmpty()) {
                        continue;
                    }

                    System.out.println("RECEIVED: " + line);
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
