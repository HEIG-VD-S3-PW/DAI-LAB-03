package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.objects.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

public class WatchCommand extends Command {
    private static final int BUFFER_SIZE = 8192;
    private static final String END_MARKER = "END_OF_STREAM";

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
        File videoFile = new File(video.getURL());

        try {
            // Envoyer la taille du fichier
            sendResponse(new CommandResponse(CommandResponseCode.OK, String.valueOf(videoFile.length())));

            // Envoyer le fichier en chunks encodés en Base64
            try (FileInputStream fis = new FileInputStream(videoFile)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    String encodedChunk = Base64.getEncoder().encodeToString(
                            bytesRead < buffer.length ?
                                    java.util.Arrays.copyOf(buffer, bytesRead) :
                                    buffer
                    );
                    out.write(encodedChunk + "\n");
                    out.flush();
                }
                // Envoyer un marqueur de fin
                out.write(END_MARKER + "\n");
                out.flush();
            }

            return null;
        } catch (IOException e) {
            return new CommandResponse(CommandResponseCode.ERROR, "Error streaming video: " + e.getMessage());
        }
    }

    @Override
    public void receive() {
        File tempFile = null;
        Process process = null;

        try {
            CommandResponse response = readResponse();
            if (response.getCode() != 200) {
                System.out.println(response.getMessage());
                return;
            }

            long fileSize = Long.parseLong(response.getMessage());
            tempFile = File.createTempFile("video_", ".mp4");
            tempFile.deleteOnExit();

            // Recevoir le fichier
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                String line;
                long totalReceived = 0;

                while ((line = in.readLine()) != null) {
                    if (line.equals(END_MARKER)) {
                        break;
                    }

                    byte[] chunk = Base64.getDecoder().decode(line);
                    fos.write(chunk);
                    totalReceived += chunk.length;

                    System.out.printf("\rDownloading: %.1f%%", (totalReceived * 100.0) / fileSize);
                }
                System.out.println("\nDownload complete!");
            }

            String absolutePath = tempFile.getAbsolutePath();
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", absolutePath);
            } else if (os.contains("mac")) {
                processBuilder = new ProcessBuilder("open", absolutePath);
            } else {
                processBuilder = new ProcessBuilder("xdg-open", absolutePath);
            }

            process = processBuilder.start();

            // Attendre un peu pour laisser le temps au lecteur de démarrer
            Thread.sleep(2000);


        } catch (Exception e) {
            System.err.println("Error while watching video: " + e.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
            if (tempFile != null && tempFile.exists()) {
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException e) {
                    System.err.println("Error deleting temp file: " + e.getMessage());
                }
            }
        }
    }
}