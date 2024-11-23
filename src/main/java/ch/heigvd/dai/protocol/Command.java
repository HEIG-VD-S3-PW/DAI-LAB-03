package ch.heigvd.dai.protocol;

import ch.heigvd.dai.User;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.stream.Stream;

public abstract class Command {

    protected final String name;
    protected final String description;
    protected BufferedReader in;
    protected PrintWriter out;

    public Command(String name, String description){
        this.name = name;
        this.description = description;
    }

    public void setIOStreams(BufferedReader in, PrintWriter out){
        this.in = in;
        this.out = out;
    }

    public abstract void validate(String[] args) throws CommandException;

    public abstract CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args);


}
