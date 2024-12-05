package ch.heigvd.dai.protocol;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.server.ServerCommandHandler;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class Command {

    protected final String name;
    protected final String description;
    protected BufferedReader in;
    protected BufferedWriter out;

    public Command(String name, String description){
        this.name = name;
        this.description = description;
    }

    public void setIOStreams(BufferedReader in, BufferedWriter out){
        this.in = in;
        this.out = out;
    }

    protected void sendResponse(CommandResponse response) throws IOException {
        out.write(response.getCode() + " " + response.getMessage() + "\n");
        out.flush();
    }

    protected CommandResponse readResponse() throws IOException {
        String responseLine = in.readLine();
        if (responseLine == null) {
            throw new IOException("Connexion fermée");
        }

        String[] parts = responseLine.split(" ", 2);
        int code = Integer.parseInt(parts[0]);
        String message = parts.length > 1 ? parts[1] : "";

        // Convertir le code numérique en CommandResponseCode
        CommandResponseCode responseCode = null;
        for (CommandResponseCode c : CommandResponseCode.values()) {
            if (c.getCode() == code) {
                responseCode = c;
                break;
            }
        }

        if (responseCode == null) {
            throw new IOException("Code de réponse invalide: " + code);
        }

        return new CommandResponse(responseCode, message);
    }

    public abstract void validate(String[] args) throws CommandException;

    public abstract CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args);

    public abstract void receive();

    protected void updateServerState(ServerCommandHandler handler, User newUser) {
        handler.setUser(newUser);
    }

}
