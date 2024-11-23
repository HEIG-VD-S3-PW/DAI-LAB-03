package ch.heigvd.dai.process;

import ch.heigvd.dai.User;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class SignInServerProcess extends Process {

    private StreamingVideo streamingVideo;
    private User user;

    public SignInServerProcess(BufferedReader in, PrintWriter out, StreamingVideo streamingVideo) {
        super(in, out);
        this.streamingVideo = streamingVideo;
    }

    @Override
    public void execute() throws Exception {

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

        this.user = user;

    }

    public User getUser() {
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
