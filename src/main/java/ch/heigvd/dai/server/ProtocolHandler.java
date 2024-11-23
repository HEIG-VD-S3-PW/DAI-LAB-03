package ch.heigvd.dai.server;

import ch.heigvd.dai.User;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandRegistry;
import ch.heigvd.dai.protocol.CommandResponse;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ProtocolHandler {
    private final CommandRegistry registry;
    private final BufferedReader in;
    private final PrintWriter out;
    private final User user;

    public ProtocolHandler(CommandRegistry registry, BufferedReader in, PrintWriter out, User user) {
        this.registry = registry;
        this.in = in;
        this.out = out;
        this.user = user;
    }

    public void handleLine(String line) {
        String[] parts = line.split(" ", 2);
        String commandName = parts[0].toUpperCase();
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

        Command command = registry.getCommand(commandName);
        if (command == null) {
            sendResponse(new CommandResponse(400, "Commande inconnue: " + commandName));
            return;
        }

        try {
            // Validation des arguments
            command.validate(args);

            // Exécution
            CommandResponse response = command.execute(user, args);

            // Envoi de la réponse
            sendResponse(response);

        } catch (Exception e) {
            sendResponse(new CommandResponse(500, "Erreur serveur: " + e.getMessage()));
        }
    }

    private void sendResponse(CommandResponse response) {
        out.println(response.getCode() + " " + response.getMessage());
        out.flush();
    }
}