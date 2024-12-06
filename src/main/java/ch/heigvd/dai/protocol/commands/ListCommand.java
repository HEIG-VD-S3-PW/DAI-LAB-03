package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.objects.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ListCommand extends Command {
    public ListCommand() {
        super("LIST", "List all the files in the current directory");
    }

    @Override
    public void validate(String[] args) throws CommandException {}

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {

        if(streamingVideo.getVideos().isEmpty()){
            return new CommandResponse(CommandResponseCode.NOT_FOUND, "No videos found");
        }

        StringBuilder response = new StringBuilder();
        for(int i = 0; i < streamingVideo.getVideos().size(); i++){
            Video video = streamingVideo.getVideos().get(i);

            // Format brut: index,titre,description;
            response.append(i + 1).append(",")
                    .append(video.getTitle()).append(",")
                    .append(video.getDescription()).append(";");
        }

        return new CommandResponse(CommandResponseCode.OK, Base64.getEncoder().encodeToString(response.toString().getBytes()));
    }

    @Override
    public void receive() {
        try {

            CommandResponse response = readResponse();

            if(response.getCode() != 200){
                System.err.println(response.getMessage());
                return;
            }

            String list = new String(Base64.getDecoder().decode(response.getMessage()), StandardCharsets.UTF_8);
            String[] videos = list.split(";");

            for(String video : videos) {
                if(!video.isEmpty()) {
                    String[] parts = video.split(",");
                    if(parts.length == 3) {
                        System.out.println(parts[0] + ") " + parts[1] + " - " + parts[2]);
                    }
                }
            }


        } catch (IOException e) {
            System.err.println("Error while reading response: " + e.getMessage());
        }
    }
}