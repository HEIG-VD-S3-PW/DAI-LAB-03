package ch.heigvd.dai.protocol;

import ch.heigvd.dai.protocol.commands.WatchCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry() {
        registerCommand(new WatchCommand());

    }

    public void registerCommand(Command command) {
        commands.put(command.name.toUpperCase(), command);
    }

    public Command getCommand(String name) {
        return commands.get(name.toUpperCase());
    }
}
