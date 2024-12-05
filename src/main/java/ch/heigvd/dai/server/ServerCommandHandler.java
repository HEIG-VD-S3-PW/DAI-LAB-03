package ch.heigvd.dai.server;

import ch.heigvd.dai.User;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandRegistry;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ServerCommandHandler {
    private final CommandRegistry registry;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final User user;
    private final StreamingVideo streamingVideo;

    public ServerCommandHandler(CommandRegistry registry, BufferedReader in, BufferedWriter out, User user, StreamingVideo streamingVideo) {
        this.registry = registry;
        this.in = in;
        this.out = out;
        this.user = user;
        this.streamingVideo = streamingVideo;
    }

    public void handleLine(String line) throws IOException {

        String[] parts = line.split(" ", 2);
        String commandName = parts[0].toUpperCase();
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

        Command command = registry.getCommand(commandName);
        if (command == null) {
            sendResponse(new CommandResponse(CommandResponseCode.ERROR, "Commande inconnue: " + commandName));
            return;
        }

        try {
            // Validation des arguments
            command.validate(args);

            // Exécution
            CommandResponse response = command.execute(user, streamingVideo, args);

            // Envoi de la réponse
            if (response != null){
                sendResponse(response);
            }

        } catch (Exception e) {
            sendResponse(new CommandResponse(CommandResponseCode.ERROR, "Erreur serveur: " + e.getMessage()));
        }
    }

    private void sendResponse(CommandResponse response) throws IOException {
        out.write(response.getCode() + " " + response.getMessage() + "\n");
        out.flush();
    }
}