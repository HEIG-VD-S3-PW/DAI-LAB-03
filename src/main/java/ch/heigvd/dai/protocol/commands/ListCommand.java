package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.IOException;

public class ListCommand extends Command {
    public ListCommand() {
        super("LIST", "List all the files in the current directory");
    }

    @Override
    public void validate(String[] args) throws CommandException {}

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {
        StringBuilder response = new StringBuilder();
        for(int i = 0; i < streamingVideo.getVideos().size(); i++){
            Video video = streamingVideo.getVideos().get(i);
            // Format brut: index,titre,description;
            response.append(i + 1).append(",")
                    .append(video.getTitle()).append(",")
                    .append(video.getDescription()).append(";");
        }

        return new CommandResponse(200, response.toString());
    }

    @Override
    public CommandResponse receive() {
        try {
            CommandResponse response = readResponse();

            if (response.getCode() == 200) {
                String[] videos = response.getMessage().split(";");

                for(String video : videos) {
                    if(!video.isEmpty()) {
                        // Split par "," pour avoir index, titre, description
                        String[] parts = video.split(",");
                        if(parts.length == 3) {
                            System.out.println(parts[0] + ") " + parts[1] + " - " + parts[2]);
                        }
                    }
                }
            } else {
                System.out.println("Error: " + response.getMessage());
            }

            return response;

        } catch (IOException e) {
            System.err.println("Error while listing videos: " + e.getMessage());
            return new CommandResponse(500, "Error while listing videos");
        }
    }
}