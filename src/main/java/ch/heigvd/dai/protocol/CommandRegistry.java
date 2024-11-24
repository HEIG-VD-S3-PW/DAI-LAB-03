package ch.heigvd.dai.protocol;

import ch.heigvd.dai.protocol.commands.DeleteCommand;
import ch.heigvd.dai.protocol.commands.ListCommand;
import ch.heigvd.dai.protocol.commands.WatchCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private final BufferedReader in;
    private final BufferedWriter out;

    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry(BufferedReader in, BufferedWriter out) {
        this.in = in;
        this.out = out;

        registerCommand(new WatchCommand());
        registerCommand(new ListCommand());
        registerCommand(new DeleteCommand());
    }

    public void registerCommand(Command command) {
        commands.put(command.name.toUpperCase(), command);
        command.setIOStreams(in, out);
    }

    public Command getCommand(String name) {
        return commands.get(name.toUpperCase());
    }
}
