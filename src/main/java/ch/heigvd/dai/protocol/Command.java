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
        Utils.send(out, response.getCode() + " " + response.getMessage());
    }

    public CommandResponse readResponse() throws IOException {
        return Utils.readResponse(in);
    }

    public abstract void validate(String[] args) throws CommandException;

    // Server side execution
    public abstract CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args);

    // Client side execution
    public abstract void receive();

}
