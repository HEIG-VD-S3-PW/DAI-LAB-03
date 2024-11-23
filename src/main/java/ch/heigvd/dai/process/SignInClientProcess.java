package ch.heigvd.dai.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Scanner;

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

        do{
            System.out.print("Enter your pseudo: ");
            String pseudo = scanner.nextLine();
            out.write(pseudo + "\n");
            out.flush();
            response = in.readLine();

        }while(response.equals("Invalid entry."));

        do{
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            out.write(email + "\n");
            out.flush();
            response = in.readLine();

        }while(response.equals("Invalid entry."));
    }

}
