package ch.heigvd.dai.utils;

import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Utils {

    private final static String DELIMITER = "\n";

    public static boolean emailValidation(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static String askForInput(String message, Predicate<String> validator) {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        do {
            System.out.print(message);
            input = scanner.nextLine();
        } while(input.isEmpty() || (validator != null && !validator.test(input)));
        return input;
    }

    public static CommandResponse readResponse(BufferedReader in) throws IOException {
        String responseLine = in.readLine();
        if (responseLine == null) {
            throw new IOException("Connection closed");
        }

        String[] parts = responseLine.split(" ", 2);
        int code = Integer.parseInt(parts[0]);
        String message = parts.length > 1 ? parts[1] : "";

        CommandResponseCode responseCode = CommandResponseCode.fromCode(code);

        if (responseCode == null) {
            throw new IOException("Invalid response code: " + code);
        }

        return new CommandResponse(responseCode, message);
    }


    public static void send(BufferedWriter in, Object message) throws IOException {
        in.write(message + DELIMITER);
        in.flush();
    }


}
