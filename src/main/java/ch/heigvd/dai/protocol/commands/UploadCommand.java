package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.server.StreamingVideo;

public class UploadCommand extends Command {

    public UploadCommand(){
        super("UPLOAD", "Upload video");
    }

    @Override
    public void validate(String[] args) throws CommandException {
        if(args.length < 3){
            return;
        }
    }

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {




        return null;
    }

    @Override
    public void receive() {

    }
}
