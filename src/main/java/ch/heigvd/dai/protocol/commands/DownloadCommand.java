package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.objects.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.StreamingVideo;
import ch.heigvd.dai.utils.Utils;

import java.io.*;
import java.util.Base64;

public class DownloadCommand extends Command {

    private static final int BUFFER_SIZE = 8192;

    public DownloadCommand() {
        super("DOWNLOAD", "Download a video");
    }

    @Override
    public void validate(String[] args) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("The download command expects exactly one argument");
        }
    }

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {

        String videoChoice = args[0];

        if (!streamingVideo.isValidChoice(videoChoice)) {
            return new CommandResponse(CommandResponseCode.NOT_FOUND, "Video not found");
        }

        Video video = streamingVideo.getVideo(videoChoice);

        if (!streamingVideo.canWatchVideo(video.getTitle())) {
            return new CommandResponse(CommandResponseCode.FORBIDDEN, "Video is currently being deleted or is unavailable");
        }

        File videoFile = new File(video.getURL());

        try {

            // Send the video title
            sendResponse(new CommandResponse(CommandResponseCode.OK, video.getTitle()));

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

                Utils.send(out, Utils.DOWNLOAD_DELIMITER);
            }

            return null;

        } catch (IOException e) {
            return new CommandResponse(CommandResponseCode.ERROR, "Error streaming video: " + e.getMessage());

        }finally {
            streamingVideo.finishWatchingVideo(video.getTitle());
        }
    }

    @Override
    public void receive() {

        try {

            CommandResponse response = readResponse();

            if (response.getCode() != 200) {
                System.out.println(response.getMessage());
                return;
            }

            File file = new File(Utils.CLIENT_DATA_PATH + "/" + response.getMessage() + ".mp4");

            if (file.exists()) {
                file.delete();
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {

                String line;

                while ((line = in.readLine()) != null) {
                    if (line.equals(Utils.DOWNLOAD_DELIMITER)) {
                        break;
                    }

                    byte[] chunk = Base64.getDecoder().decode(line);
                    fos.write(chunk);
                }

                System.out.println("Download complete !");

            }

        } catch (Exception e) {
            System.err.println("Error while watching video: " + e.getMessage());
        }
    }

}