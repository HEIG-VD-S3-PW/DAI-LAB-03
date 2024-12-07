package ch.heigvd.dai.protocol;

import ch.heigvd.dai.protocol.commands.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private final BufferedReader in;
    private final BufferedWriter out;

    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry(BufferedReader in, BufferedWriter out) {
        this.in = in;
        this.out = out;

        registerCommand(new DownloadCommand());
        registerCommand(new ListCommand());
        registerCommand(new DeleteCommand());
        registerCommand(new ConnectCommand());
        registerCommand(new UploadCommand());
        registerCommand(new QuitCommand());
    }

    public void registerCommand(Command command) {
        commands.put(command.name.toUpperCase(), command);
        command.setIOStreams(in, out);
    }

    public Command getCommand(String name) {
        return commands.get(name.toUpperCase());
    }

    public Map<String, Command> getCommands() {
        return commands;
    }
}
