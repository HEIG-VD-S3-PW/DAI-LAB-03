package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.server.StreamingVideo;

public class DeleteCommand extends Command {
    public DeleteCommand() {
        super("DELETE", "Delete a video");
    }

    @Override
    public void validate(String[] args) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("The delete command expects exactly one argument");
        }
    }

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {
        String videoChoice = args[0];
        if (!streamingVideo.checkValidity(videoChoice)) {
            return new CommandResponse(404, "Video not found");
        }

        streamingVideo.getVideos().remove(Integer.parseInt(videoChoice) - 1);

        return new CommandResponse(200, "Video deleted");
    }

    @Override
    public void receive() {
        try {
            CommandResponse response = readResponse();

            if(response.getCode() != 200){
                System.err.println("Error while deleting video: " + response.getMessage());
                return;
            }

            System.out.println(response.getMessage());

        } catch (Exception e) {
            System.err.println("Error while deleting video: " + e.getMessage());
        }
    }
}