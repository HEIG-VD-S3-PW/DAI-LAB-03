package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.server.StreamingVideo;

public class QuitCommand extends Command {
    public QuitCommand() { super("QUIT", "Close connection with the server"); }

    @Override
    public void validate(String[] args) throws CommandException {
        if (args.length != 0) {
            throw new CommandException("The QUIT command expects no argument");
        }
    }

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {
        return null;
    }

    @Override
    public void receive() {
        try {
            CommandResponse response = readResponse();
            if(response.getCode() != 200){
               System.err.println("Error while quitting service: " + response.getMessage());
               return;
            }
            System.out.println(response.getMessage());
        } catch (Exception e) {
            System.err.println("Error while quitting services: " + e.getMessage());
        }
    }
}
