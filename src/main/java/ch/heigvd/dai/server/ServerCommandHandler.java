package ch.heigvd.dai.server;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandRegistry;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.protocol.commands.ConnectCommand;
import ch.heigvd.dai.protocol.commands.QuitCommand;
import ch.heigvd.dai.utils.Utils;

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

    /**
     * Handle a line received from the client
     *
     * @param line The line received
     * @return True if the client wants to quit, false otherwise
     * @throws IOException
     */
    public boolean handleLine(String line) throws IOException {

        String[] parts = line.split(" ", 2);
        String commandName = parts[0].toUpperCase();
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

        Command command = registry.getCommand(commandName);
        if (command == null) {
            sendResponse(new CommandResponse(CommandResponseCode.ERROR, "Unknown command : " + commandName));
            return false;
        }

        if(user == null && !(command instanceof ConnectCommand || command instanceof QuitCommand)){
            sendResponse(new CommandResponse(CommandResponseCode.UNAUTHORIZED, "You have to be connected to execute this command"));
            return false;
        }

        String sender = user != null ? user.getUsername() : "Not Authenticated";
        System.out.println("[" + sender + "] " + line);

        try {

            command.validate(args);

            CommandResponse response = command.execute(user, streamingVideo, args);

            if(command instanceof ConnectCommand connectCommand && user == null){
                this.user = connectCommand.getCreatedUser();
            }

            if (response != null){
                sendResponse(response);
            }

            if(command instanceof QuitCommand){
                return true;
            }

        } catch (Exception e) {
            sendResponse(new CommandResponse(CommandResponseCode.ERROR, "Server error : " + e.getMessage()));
        }

        return false;
    }

    /**
     * Send a response to the client
     *
     * @param response The response to send
     * @throws IOException
     */
    private void sendResponse(CommandResponse response) throws IOException {
        Utils.send(out, response.getCode() + " " + response.getMessage());
    }


}