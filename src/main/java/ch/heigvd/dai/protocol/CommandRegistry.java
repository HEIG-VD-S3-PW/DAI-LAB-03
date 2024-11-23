package ch.heigvd.dai.protocol;

import ch.heigvd.dai.protocol.commands.WatchCommand;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private final BufferedReader in;
    private final PrintWriter out;

    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;

        registerCommand(new WatchCommand());
    }

    public void registerCommand(Command command) {
        commands.put(command.name.toUpperCase(), command);
        command.setIOStreams(in, out);
    }

    public Command getCommand(String name) {
        return commands.get(name.toUpperCase());
    }
}
