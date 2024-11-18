package ch.heigvd.dai;

// https://medium.com/@gaurangjotwani/creating-a-tcp-connection-between-two-servers-in-java-27fabe53deaa

import ch.heigvd.dai.commands.Server;

import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class TCPServer {
    private int port;
    private ServerSocket serverSocket;
    private StreamingVideo streamingVideo;

    public TCPServer(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        streamingVideo = new StreamingVideo();
        initServer();

        System.out.println("Server is running and waiting for client connection...");

        // Accept incoming client connection
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected!");

        // Setup input and output streams for communication with the client
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        out.println("Welcome to the Amar Streaming Platform !");

        /* ---------------- Manage authentification ---------------- */

        // Read message from client, contains its pseudo
        String pseudo = in.readLine();

        while(pseudo.isEmpty()) {
            out.println("Invalid entry.");
        }

        out.println("Valid pseudo");

        System.out.println("Client pseudo is : " + pseudo);

        String email = in.readLine();

        while(!emailValidation(email)){
            out.println("Invalid entry.");
            email = in.readLine();
        }

        out.println("Valid email");

        System.out.println("Client email is : " + email);

        streamingVideo.addUser(new User(pseudo, email));


        /* ---------------- Manage Video choice ---------------- */
        String videos = "";
        int index = 1;

        for(Video v : streamingVideo.getVideos()){
            videos += index++ + ". " + v.toString() + "\n";
        }

        out.println("Please choose between one of the following videos:\n" + videos);
        out.println("end");

        String videoChoice = in.readLine();
        while(!checkValidity(videoChoice)){
            out.println("Invalid entry.");
            videoChoice = in.readLine();
        }

        out.println("Valid choice");

        // Close the client socket
        clientSocket.close();
        // Close the server socket
        serverSocket.close();
    }

    private void initServer(){
        String videoPath = System.getProperty("user.dir") + "/videos";

        streamingVideo.addVideo(new Video("3 Minute Timer", "Displays a timer from 3 minutes to 0", videoPath + "video1.mp4"));
        streamingVideo.addVideo(new Video("Google Office tour", "Visit of Google's building", videoPath + "video2.mp4"));
        streamingVideo.addVideo(new Video("L'entretien - Choss", "Vidéo de Choss sur un entretien", videoPath + "video3.mp4"));
        streamingVideo.addVideo(new Video("Le Clown - Choss", "Vidéo de Choss sur un clown", videoPath + "video4.mp4"));
        streamingVideo.addVideo(new Video("Why is Switzerland home to so many billionaires", "Documentary on Switzerland's billionaires", videoPath + "video5.mp4"));
    }

    private boolean emailValidation(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private boolean checkValidity(String videoChoice){
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
