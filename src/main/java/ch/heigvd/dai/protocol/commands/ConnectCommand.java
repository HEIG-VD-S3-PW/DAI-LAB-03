package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.commands.Server;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.ServerCommandHandler;
import ch.heigvd.dai.server.StreamingVideo;

import ch.heigvd.dai.Utils;

public class ConnectCommand extends Command {


    private User createdUser;

    public ConnectCommand() {
        super("CONNECT", "Connect the client with the server");
    }

    @Override
    public void validate(String[] args) throws CommandException {
        if (args.length != 2) {
            throw new CommandException("The delete command expects exactly one argument");
        }
    }

    @Override
    public CommandResponse execute(StreamingVideo streamingVideo, String[] args) {
        String pseudo = args[0];
        String email = args[1];

        if(pseudo.isEmpty()){
            return new CommandResponse(CommandResponseCode.ERROR, "Invalid pseudo");
        }

        if (!Utils.emailValidation(email)) {
            return new CommandResponse(CommandResponseCode.ERROR, "Invalid email address");
        }

        if(streamingVideo.userExists(pseudo, email)){
            return new CommandResponse(CommandResponseCode.ERROR, "User already exists");
        }

        User newUser = new User(pseudo, email);
        createdUser = newUser;
        streamingVideo.addUser(newUser);

        return new CommandResponse(CommandResponseCode.OK, "Connection successful");
    }

    @Override
    public void receive() {
        try {
            CommandResponse response = readResponse();

            if(response.getCode() != 200){
                System.err.println("Error while connecting the user: " + response.getMessage());
                return;
            }

            System.out.println("Vous êtes connectés !");

        } catch (Exception e) {
            System.err.println("Error while connecting the user: " + e.getMessage());
        }
    }

    public User getCreatedUser() {
        return createdUser;
    }
}