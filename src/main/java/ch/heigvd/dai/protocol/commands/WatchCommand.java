package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.server.StreamingVideo;

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
        out.println("Watching video " + video.getURL());

        return new CommandResponse(500, "Watching video " + args[0]);
    }



}