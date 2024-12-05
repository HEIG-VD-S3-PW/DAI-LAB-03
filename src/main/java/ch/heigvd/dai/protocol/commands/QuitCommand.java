package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.StreamingVideo;

public class QuitCommand extends Command {


    public QuitCommand() { super("QUIT", "Close connection with the server"); }

    @Override
    public void validate(String[] args) throws CommandException {
        if (args.length != 0) args = new String[0];
    }

    @Override
    public CommandResponse execute(StreamingVideo streamingVideo, String[] args) {
        try {
            // if (!streamingVideo.suppressUser(user)) return new CommandResponse(CommandResponseCode.NOT_FOUND, "User not found");
        } catch (Exception e) {
            return new CommandResponse(CommandResponseCode.ERROR, e.getMessage());
        }
        return new CommandResponse(CommandResponseCode.OK, "See you soon :)");
    }

    @Override
    public void receive() {
        try {
            CommandResponse response = readResponse();
            if(response.getCode() != 200){
               System.err.println("Error while quitting ASP: " + response.getMessage() +
                                "\nYou won't be able to reconnect with the same username and email");
               return;
            }
            System.out.println(response.getMessage());
        } catch (Exception e) {
            System.err.println("Error while quitting ASP: " + e.getMessage());
        }
    }
}
