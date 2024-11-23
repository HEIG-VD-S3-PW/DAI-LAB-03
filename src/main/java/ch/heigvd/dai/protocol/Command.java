package ch.heigvd.dai.protocol;

import ch.heigvd.dai.User;

public abstract class Command {

    protected final String name;
    protected final String description;

    public Command(String name, String description){
        this.name = name;
        this.description = description;
    }

    public abstract void validate(String[] args) throws CommandException;

    public abstract CommandResponse execute(User user, String[] args);


}
