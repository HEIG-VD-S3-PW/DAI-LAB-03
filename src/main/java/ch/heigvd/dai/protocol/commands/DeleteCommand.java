package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.objects.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.StreamingVideo;

public class DeleteCommand extends Command {
    public DeleteCommand() {
        super("DELETE", "Delete a video");
    }

    @Override
    public void validate(String[] args) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("The delete command expects exactly one argument (DELETE <videoChoice>)");
        }
    }

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {

        String videoChoice = args[0];

        if (!streamingVideo.isValidChoice(videoChoice)) {
            return new CommandResponse(CommandResponseCode.NOT_FOUND, "Video not found");
        }

        Video video = streamingVideo.getVideo(videoChoice);

        if (!streamingVideo.canDeleteVideo(video.getTitle())) {
            return new CommandResponse(CommandResponseCode.FORBIDDEN, "Video is currently being downloaded by other users");
        }

        streamingVideo.deleteVideo(video);

        return new CommandResponse(CommandResponseCode.OK, "Video deleted");
    }

    @Override
    public void receive() {
        try {

            CommandResponse response = readResponse();
            System.out.println(response.getMessage());

        } catch (Exception e) {
            System.err.println("Error while reading response: " + e.getMessage());
        }
    }
}