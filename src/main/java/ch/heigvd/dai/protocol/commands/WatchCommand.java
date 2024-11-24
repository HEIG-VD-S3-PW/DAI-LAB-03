package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
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
            return new CommandResponse(404, "Video not found");
        }

        Video video = streamingVideo.getVideo(videoChoice);

        try {
            sendResponse(new CommandResponse(200, "Starting video stream: " + video.getTitle()));

            byte[] videoData = Files.readAllBytes(Path.of(video.getURL()));
            String encodedVideo = Base64.getEncoder().encodeToString(videoData);

            out.write(encodedVideo + "\n");
            out.flush();

            return new CommandResponse(200, "Video stream completed");

        } catch (IOException e) {
            return new CommandResponse(500, "Error streaming video: " + e.getMessage());
        }
    }

    @Override
    public CommandResponse receive() {
        File tempFile = null;
        Process vlcProcess = null;

        try {

            CommandResponse initialResponse = readResponse();
            if (initialResponse.getCode() != 200) {
                System.out.println(initialResponse.getMessage());
                return initialResponse;
            }

            tempFile = File.createTempFile("video_", ".mp4");
            tempFile.deleteOnExit();

            String encodedVideo = in.readLine();
            byte[] videoData = Base64.getDecoder().decode(encodedVideo);

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(videoData);
                fos.flush();
            }

            ProcessBuilder vlcBuilder = new ProcessBuilder("vlc", tempFile.getAbsolutePath());
            vlcProcess = vlcBuilder.start();
            vlcProcess.waitFor();

            CommandResponse finalResponse = readResponse();
            System.out.println(finalResponse.getMessage());
            return finalResponse;

        } catch (Exception e) {
            System.err.println("Error while watching video: " + e.getMessage());
            return new CommandResponse(500, "Error while watching video");
        } finally {
            if (vlcProcess != null) {
                vlcProcess.destroy();
            }
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                    System.out.println("Temporary file deleted");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}