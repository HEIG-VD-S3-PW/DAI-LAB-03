package ch.heigvd.dai.protocol.commands;

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
    public CommandResponse execute(String[] args) {



        return new CommandResponse("Watching video " + args[0], true);
    }
}
