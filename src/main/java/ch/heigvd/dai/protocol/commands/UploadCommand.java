package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UploadCommand extends Command {
    private static final int BUFFER_SIZE = 8192;
    private static final String END_MARKER = "END_OF_STREAM";

    public UploadCommand() {
        super("UPLOAD", "Upload video");
    }

    @Override
    public void validate(String[] args) throws CommandException {
        if (args.length != 2) {
            throw new CommandException("The upload command expects title and description");
        }
    }

    @Override
    public CommandResponse execute(StreamingVideo streamingVideo, String[] args) {
        String title = new String(Base64.getDecoder().decode(args[0]), StandardCharsets.UTF_8);
        String description = new String(Base64.getDecoder().decode(args[1]), StandardCharsets.UTF_8);

        System.out.println("Receiving video: " + title + " with description: " + description);

        // Création du nom de fichier encodé
        String fileData = title + "|" + description;
        String encodedFileName = Base64.getEncoder().encodeToString(fileData.getBytes(StandardCharsets.UTF_8));
        String fullFileName = encodedFileName + ".mp4";

        try {
            // Lire la taille du fichier
            String sizeLine = in.readLine();
            long fileSize = Long.parseLong(sizeLine);
            System.out.println("Expected file size: " + fileSize + " bytes");

            // Créer le fichier de destination
            try (FileOutputStream fos = new FileOutputStream("videos/" + fullFileName)) {
                String line;
                long totalReceived = 0;

                while ((line = in.readLine()) != null) {
                    if (line.equals(END_MARKER)) {
                        break;
                    }

                    byte[] chunk = Base64.getDecoder().decode(line);
                    fos.write(chunk);
                    totalReceived += chunk.length;

                    System.out.printf("\rReceiving: %.1f%%", (totalReceived * 100.0) / fileSize);
                }
                System.out.println("\nUpload complete!");
            }

            streamingVideo.addVideo(new Video(title, description, fullFileName));
            return new CommandResponse(CommandResponseCode.OK, "Video uploaded successfully");

        } catch (Exception e) {
            System.err.println("Error during upload: " + e.getMessage());
            return new CommandResponse(CommandResponseCode.ERROR, "Error uploading video: " + e.getMessage());
        }
    }

    @Override
    public void receive() {
        try {
            CommandResponse response = readResponse();
            if (response.getCode() != 200) {
                System.err.println("Error while uploading video: " + response.getMessage());
                return;
            }
            System.out.println(response.getMessage());
        } catch (IOException e) {
            System.err.println("Error while uploading video: " + e.getMessage());
        }
    }
}