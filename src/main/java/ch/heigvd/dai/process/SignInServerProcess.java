package ch.heigvd.dai.process;

import ch.heigvd.dai.User;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class SignInServerProcess extends Process {

    private StreamingVideo streamingVideo;
    private User user;

    public SignInServerProcess(BufferedReader in, BufferedWriter out, StreamingVideo streamingVideo) {
        super(in, out);
        this.streamingVideo = streamingVideo;
    }

    @Override
    public void execute() throws Exception {

        // Read message from client, contains its pseudo
        String pseudo = in.readLine();

        while(pseudo.isEmpty()) {
            out.write("Invalid entry." + "\n");
            out.flush();
        }

        out.write("Valid pseudo" + "\n");
        out.flush();

        System.out.println("Client pseudo is : " + pseudo);

        String email = in.readLine();

        while(!emailValidation(email)){
            out.write("Invalid entry." + "\n");
            out.flush();
            email = in.readLine();
        }

        out.write("Valid email" + "\n");
        out.flush();

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
