package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;

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
    public CommandResponse execute(User user, String[] args) {
        System.out.println("Watching video " + args[0]);
        return new CommandResponse(500, "Watching video " + args[0]);
    }
}
