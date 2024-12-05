package ch.heigvd.dai.process;

import ch.heigvd.dai.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class SignInClientProcess extends Process {

    public SignInClientProcess(BufferedReader in, BufferedWriter out) {
        super(in, out);
    }

    @Override
    public boolean execute() throws Exception {

        String pseudo = Utils.askForInput("Enter your pseudo: ", null);
        String email = Utils.askForInput("Enter your email: ", Utils::emailValidation);

        Utils.send(out, "SIGNIN " + pseudo + " " + email);

        return true;

    }


}
