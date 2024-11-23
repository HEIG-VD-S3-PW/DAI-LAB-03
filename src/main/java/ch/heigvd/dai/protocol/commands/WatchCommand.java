package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.*;

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
        if(!streamingVideo.checkValidity(videoChoice)){
            return new CommandResponse(404, "Video not found");
        }

        Video video = streamingVideo.getVideo(videoChoice);

        try (BufferedReader fileReader = new BufferedReader(new FileReader(video.getURL()), 8192)) {

            char[] buffer = new char[8192];
            int charsRead;

            while ((charsRead = fileReader.read(buffer)) != -1) {
                out.write(buffer, 0, charsRead);
                out.flush();

            }

            // Flush final pour s'assurer que tout est envoyé
            out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return new CommandResponse(500, "Watching video " + video.getTitle());

    }



}
