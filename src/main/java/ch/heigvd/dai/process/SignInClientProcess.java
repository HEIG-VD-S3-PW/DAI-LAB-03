package ch.heigvd.dai.process;

import ch.heigvd.dai.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SignInClientProcess extends Process {

    public SignInClientProcess(BufferedReader in, BufferedWriter out) {
        super(in, out);
    }

    @Override
    public void execute() throws Exception {

        String pseudo = Utils.askForInput("Enter your pseudo: ", null);
        String email = Utils.askForInput("Enter your email: ", Utils::emailValidation);

        out.write("CONNECT" + " " + pseudo + " " + email + "\n");
        out.flush();

    }


}
