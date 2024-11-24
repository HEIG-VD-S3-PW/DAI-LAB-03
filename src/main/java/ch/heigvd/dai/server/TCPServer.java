package ch.heigvd.dai.server;

// https://www.geeksforgeeks.org/multithreaded-servers-in-java/

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.process.SignInServerProcess;
import ch.heigvd.dai.protocol.CommandRegistry;

import java.net.*;
import java.io.*;
import java.util.regex.Pattern;

public class TCPServer {
    private int port;
    private ServerSocket serverSocket;
    private static StreamingVideo streamingVideo;

    public TCPServer(int port) throws IOException {
        this.port = port;

        streamingVideo = new StreamingVideo();
        streamingVideo.load();

        try {
            // server is listening on port 1234
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
                Socket client = serverSocket.accept();

                // Displaying that new client is connected
                // to server
                System.out.println("New client connected "
                        + client.getInetAddress()
                        .getHostAddress());

                // create a new thread object
                ClientHandler clientSock
                        = new ClientHandler(client);

                // This thread will handle the client
                // separately
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
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
