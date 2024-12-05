package ch.heigvd.dai.protocol;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.server.ServerCommandHandler;
import ch.heigvd.dai.server.StreamingVideo;
import ch.heigvd.dai.utils.Utils;

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

    public CommandResponse readResponse() throws IOException {
        return Utils.readResponse(in);
    }

    public abstract void validate(String[] args) throws CommandException;

    public abstract CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args);

    public abstract void receive();

    protected void updateServerState(ServerCommandHandler handler, User newUser) {
        handler.setUser(newUser);
    }

}
