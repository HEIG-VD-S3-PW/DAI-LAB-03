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

    public UploadProcess(BufferedReader in, BufferedWriter out) {
        super(in, out);
    }

    @Override
    public boolean execute() throws Exception {
        String title = Utils.askForInput("Enter the title of the video: ", null);
        String description = Utils.askForInput("Enter the description of the video: ", null);
        String path = Utils.askForInput("Enter the path of the video file: ", null);

        File videoFile = validateFile(path);

        String encodedTitle = Base64.getEncoder().encodeToString(title.getBytes());
        String encodedDesc = Base64.getEncoder().encodeToString(description.getBytes());

        Utils.send(out, "UPLOAD " + encodedTitle + " " + encodedDesc);

        // Wait for server confirmation (to be sure the user is connected)
        CommandResponse response = Utils.readResponse(in);
        if(response.getCode() != CommandResponseCode.OK.getCode()){
            System.err.println(response.getMessage());
            return false;
        }
        System.out.println(response.getMessage());


        try (FileInputStream fis = new FileInputStream(videoFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                String encodedChunk = Base64.getEncoder().encodeToString(
                        bytesRead < buffer.length ?
                                java.util.Arrays.copyOf(buffer, bytesRead) :
                                buffer
                );
                Utils.send(out, encodedChunk);
            }

            Utils.send(out, Utils.UPLOAD_DELIMITER);

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
        if(!Utils.getFileExtension(file.getName()).equals("mp4")){
            throw new IOException("Invalid file format: " + path);
        }
        return file;
    }
}