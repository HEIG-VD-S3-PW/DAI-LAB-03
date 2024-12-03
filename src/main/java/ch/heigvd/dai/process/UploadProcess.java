

package ch.heigvd.dai.process;

import java.io.*;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

public class UploadProcess extends Process {
    private final Scanner scanner;
    private static final int CHUNK_SIZE = 8192;
    private static final int TIMEOUT_SECONDS = 30;

    public UploadProcess(BufferedReader in, BufferedWriter out) {
        super(in, out);
        scanner = new Scanner(System.in);
    }

    @Override
    public void execute() throws Exception {
        String title = getValidInput("Enter the title of the video: ");
        String description = getValidInput("Enter the description of the video: ");
        String path = getValidInput("Enter the path of the video: ");

        File videoFile = validateFile(path);
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        title = Base64.getEncoder().encodeToString(title.getBytes());
        description = Base64.getEncoder().encodeToString(description.getBytes());

        // Envoi de la commande d'upload
        out.write("UPLOAD " + title + " " + description + "\n");
        out.flush();

        try (FileInputStream fis = new FileInputStream(videoFile)) {
            long totalSize = videoFile.length();
            long uploadedSize = 0;
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;

            System.out.println("Starting upload of: " + path);
            System.out.println("Total size: " + (totalSize / 1024 / 1024) + " MB");

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] toEncode = buffer;
                if (bytesRead != buffer.length) {
                    toEncode = new byte[bytesRead];
                    System.arraycopy(buffer, 0, toEncode, 0, bytesRead);
                }

                // Mise à jour du checksum
                md.update(toEncode);

                // Envoi du chunk encodé
                String chunk = Base64.getEncoder().encodeToString(toEncode);
                out.write(chunk + "\n");
                out.flush();

                // Mise à jour de la progression
                uploadedSize += bytesRead;
                double progress = (uploadedSize * 100.0) / totalSize;
                System.out.printf("\rUpload progress: %.2f%%", progress);
            }

            // Envoi du checksum
            String checksum = Base64.getEncoder().encodeToString(md.digest());
            out.write("END_OF_UPLOAD" + "\n");
            out.flush();

            System.out.println("\nUpload completed successfully!");
        }
    }

    private String getValidInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }
        return input;
    }

    private File validateFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + path);
        }
        if (!file.canRead()) {
            throw new IOException("Cannot read file: " + path);
        }
        if (file.length() == 0) {
            throw new IOException("File is empty: " + path);
        }
        return file;
    }
}

