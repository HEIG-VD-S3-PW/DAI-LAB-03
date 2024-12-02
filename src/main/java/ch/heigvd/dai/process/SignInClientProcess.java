package ch.heigvd.dai.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SignInClientProcess extends Process {

    private final Scanner scanner;

    public SignInClientProcess(BufferedReader in, BufferedWriter out) {
        super(in, out);
        scanner = new Scanner(System.in);
    }

    @Override
    public void execute() throws Exception {

        String response = in.readLine();
        System.out.println(response);

        String pseudo = "";
        do{
            System.out.print("Enter your pseudo: ");
            pseudo = scanner.nextLine();
        }while(pseudo.isEmpty());

        String email = "";
        do{
            System.out.print("Enter your email: ");
            email = scanner.nextLine();
        }while(!emailValidation(email));

        out.write("CONNECT" + " " + pseudo + " " + email + "\n");
        out.flush();

        response = in.readLine();
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
