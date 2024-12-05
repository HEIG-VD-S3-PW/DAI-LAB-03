package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.StreamingVideo;

public class QuitCommand extends Command {


    public QuitCommand() { super("QUIT", "Close connection with the server"); }

    @Override
    public void validate(String[] args) throws CommandException {}

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {


        if(user != null && streamingVideo.userExists(user.getUsername(), user.getEmail())){
            streamingVideo.removeUser(user);
        }
        return new CommandResponse(CommandResponseCode.OK, "See you soon :)");
    }

    @Override
    public void receive() {
        try {
            CommandResponse response = readResponse();
            System.out.println(response.getMessage());
        } catch (Exception e) {
            System.err.println("Error while quitting : " + e.getMessage());
        }
    }
}
