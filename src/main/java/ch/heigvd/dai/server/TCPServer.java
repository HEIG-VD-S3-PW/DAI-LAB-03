package ch.heigvd.dai.server;

// https://www.geeksforgeeks.org/multithreaded-servers-in-java/

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;
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
        initServer();
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

            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ){


                out.println("Welcome to the Amar Streaming Platform !");

                SignInProcess signInProcess = new SignInProcess(in, out, streamingVideo);
                User user = null;
                if((user = signInProcess.start()) == null){
                    out.println("An error occurred during the sign in process.");
                    return;
                }

                CommandRegistry registry = new CommandRegistry();
                ProtocolHandler protocolHandler = new ProtocolHandler(registry, in, out, user);

                while(!clientSocket.isClosed()){

                    String line = in.readLine();
                    System.out.println("RECEIVED: " + line);
                    protocolHandler.handleLine(line);

                }



                /* ---------------- Manage Video choice ---------------- */
                /*
                String videos = "";
                int index = 1;

                for(Video v : streamingVideo.getVideos()){
                    videos += index++ + ". " + v.toString() + "\n";
                }

                out.println("\nPlease choose between one of the following videos:\n" + videos);
                out.println("end");

                String videoChoice = in.readLine();
                while(!checkValidity(videoChoice)){
                    out.println("Invalid entry.");
                    videoChoice = in.readLine();
                }

                out.println("Valid choice");

                 */

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialize the streamingVideo and add the available videos to watch
     */
    private void initServer(){
        String videoPath = System.getProperty("user.dir") + "/videos";

        streamingVideo = new StreamingVideo();

        streamingVideo.addVideo(new Video("3 Minute Timer", "Displays a timer from 3 minutes to 0", videoPath + "video1.mp4"));
        streamingVideo.addVideo(new Video("Google Office tour", "Visit of Google's building", videoPath + "video2.mp4"));
        streamingVideo.addVideo(new Video("L'entretien - Choss", "Vidéo de Choss sur un entretien", videoPath + "video3.mp4"));
        streamingVideo.addVideo(new Video("Le Clown - Choss", "Vidéo de Choss sur un clown", videoPath + "video4.mp4"));
        streamingVideo.addVideo(new Video("Why is Switzerland home to so many billionaires", "Documentary on Switzerland's billionaires", videoPath + "video5.mp4"));
    }


    /**
     * Check if the video choice is valid
     * @param videoChoice: index of the chosen video
     * @return true if the index is valid and false otherwise
     */
    private static boolean checkValidity(String videoChoice){
        int index = 0;
        try{
            index = Integer.parseInt(videoChoice);
        }
        catch(NumberFormatException e){
            return false;
        }

        return (index > 0 && index <= streamingVideo.getVideos().size());
    }
}
