package ch.heigvd.dai.server;

import ch.heigvd.dai.User;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandRegistry;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.protocol.commands.ConnectCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ServerCommandHandler {
    private final CommandRegistry registry;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final StreamingVideo streamingVideo;

    private User user;

    public ServerCommandHandler(BufferedReader in, BufferedWriter out, StreamingVideo streamingVideo) {
        this.in = in;
        this.out = out;
        this.streamingVideo = streamingVideo;

        this.registry = new CommandRegistry(in, out);
        this.user = null;
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

        if(user == null && !(command instanceof ConnectCommand)){
            sendResponse(new CommandResponse(CommandResponseCode.ERROR, "Vous devez vous connecter pour effectuer cette action"));
            return;
        }

        try {

            // Validation des arguments
            command.validate(args);

            // Exécution
            CommandResponse response = command.execute(streamingVideo, args);

            if(command instanceof ConnectCommand connectCommand && user == null){
                setUser(connectCommand.getCreatedUser());
            }

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

    public void setUser(User user) {
        this.user = user;
    }
}