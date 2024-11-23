package ch.heigvd.dai.server;

import ch.heigvd.dai.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class SignInProcess {

    private BufferedReader in;
    private PrintWriter out;
    private StreamingVideo streamingVideo;

    public SignInProcess(BufferedReader in, PrintWriter out, StreamingVideo streamingVideo) {
        this.in = in;
        this.out = out;
        this.streamingVideo = streamingVideo;
    }

    public User start() throws IOException {


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

        User user = new User(pseudo, email);

        streamingVideo.addUser(user);

        return user;

    }

    /**
     * Check if the entered email is valid
     * @param email : Email entered by the user
     * @return true if valid and false otherwise
     */
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

}
