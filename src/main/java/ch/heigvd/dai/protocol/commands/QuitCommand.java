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
        if (args != null) {
            throw new CommandException("The QUIT command expects no argument");
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
