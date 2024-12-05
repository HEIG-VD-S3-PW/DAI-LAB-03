package ch.heigvd.dai.process;

import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.utils.Utils;

import java.io.*;
import java.util.Base64;
import java.util.Scanner;

public class UploadProcess extends Process {
    private static final int BUFFER_SIZE = 8192;
    private static final String END_MARKER = "END_OF_STREAM";
    private final Scanner scanner;

    public UploadProcess(BufferedReader in, BufferedWriter out) {
        super(in, out);
        scanner = new Scanner(System.in);
    }

    @Override
    public boolean execute() throws Exception {
        String title = Utils.askForInput("Enter the title of the video: ", null);
        String description = Utils.askForInput("Enter the description of the video: ", null);
        String path = Utils.askForInput("Enter the path of the video file: ", null);

        File videoFile = validateFile(path);

        // Encoder le titre et la description en Base64
        String encodedTitle = Base64.getEncoder().encodeToString(title.getBytes());
        String encodedDesc = Base64.getEncoder().encodeToString(description.getBytes());

        // Envoyer la commande d'upload avec titre et description
        out.write("UPLOAD " + encodedTitle + " " + encodedDesc + "\n");
        out.flush();

        // Attendre la réponse du serveur
        String responseLine = in.readLine();
        if (responseLine == null) {
            throw new IOException("Connexion fermée");
        }

        String[] parts = responseLine.split(" ", 2);
        int code = Integer.parseInt(parts[0]);
        String message = parts.length > 1 ? parts[1] : "";

        if(code != CommandResponseCode.OK.getCode()){
            System.err.println(message);
            return false;
        }

        System.out.println(message);

        // Envoyer la taille du fichier
        out.write(videoFile.length() + "\n");
        out.flush();

        // Envoyer le fichier en chunks
        try (FileInputStream fis = new FileInputStream(videoFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalSent = 0;
            long fileSize = videoFile.length();

            while ((bytesRead = fis.read(buffer)) != -1) {
                String encodedChunk = Base64.getEncoder().encodeToString(
                        bytesRead < buffer.length ?
                                java.util.Arrays.copyOf(buffer, bytesRead) :
                                buffer
                );
                out.write(encodedChunk + "\n");
                out.flush();

                totalSent += bytesRead;
                System.out.printf("\rUploading: %.1f%%", (totalSent * 100.0) / fileSize);
            }

            // Envoyer le marqueur de fin
            out.write(END_MARKER + "\n");
            out.flush();

            System.out.println("\nUpload complete! Waiting for server confirmation...");
        }catch (IOException e){
            System.err.println("Error while uploading video: " + e.getMessage());
            return false;
        }

        return true;
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