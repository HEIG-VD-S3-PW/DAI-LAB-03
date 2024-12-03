package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class WatchCommand extends Command {
    public WatchCommand() {
        super("WATCH", "Watch a video");
    }

    @Override
    public void validate(String[] args) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("The watch command expects exactly one argument");
        }
    }

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {
        String videoChoice = args[0];
        if (!streamingVideo.checkValidity(videoChoice)) {
            return new CommandResponse(CommandResponseCode.NOT_FOUND, "Video not found");
        }

        Video video = streamingVideo.getVideo(videoChoice);

        try {

            sendResponse(new CommandResponse(CommandResponseCode.OK, "Starting video stream: " + videoChoice));

            try (FileInputStream fis = new FileInputStream(video.getURL())) {

                byte[] buffer = new byte[8192]; // Lecture par chunk de 8 Ko
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {

                    // Si on n'a pas lu un buffer complet, on crée un nouveau tableau de la bonne taille
                    byte[] toEncode = buffer;
                    if (bytesRead != buffer.length) {
                        toEncode = new byte[bytesRead];
                        System.arraycopy(buffer, 0, toEncode, 0, bytesRead);
                    }

                    String chunk = Base64.getEncoder().encodeToString(toEncode);
                    out.write(chunk + "\n");
                    out.flush();
                }

                out.write("END_OF_DOWNLOAD\n");
                out.flush();
            }

            return new CommandResponse(CommandResponseCode.OK, "Video stream completed");
        } catch (IOException e) {
            return new CommandResponse(CommandResponseCode.ERROR, "Error streaming video: " + e.getMessage());
        }
    }

    @Override
    public void receive() {
        File tempFile = null;
        Process vlcProcess = null;

        try {

            CommandResponse initialResponse = readResponse();
            if (initialResponse.getCode() != 200) {
                System.out.println(initialResponse.getMessage());
                return;
            }

            tempFile = File.createTempFile("video_", ".mp4");
            tempFile.deleteOnExit();


            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("END_OF_DOWNLOAD")) {
                        break;
                    }
                    byte[] chunk = Base64.getDecoder().decode(line);
                    fos.write(chunk);
                }
                fos.flush();
            }

            ProcessBuilder vlcBuilder = new ProcessBuilder("vlc", tempFile.getAbsolutePath());
            vlcProcess = vlcBuilder.start();
            vlcProcess.waitFor();

            CommandResponse finalResponse = readResponse();
            System.out.println(finalResponse.getMessage());

        } catch (Exception e) {
            System.err.println("Error while watching video: " + e.getMessage());

        } finally {
            if (vlcProcess != null) {
                vlcProcess.destroy();
            }

            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                    System.out.println("Temporary file deleted");
                } catch (IOException e) {
                    System.err.println("Error while deleting temporary file: " + e.getMessage());
                }
            }
        }

    }
}