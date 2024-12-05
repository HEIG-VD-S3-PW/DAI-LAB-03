package ch.heigvd.dai.utils;

import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Utils {

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

}
