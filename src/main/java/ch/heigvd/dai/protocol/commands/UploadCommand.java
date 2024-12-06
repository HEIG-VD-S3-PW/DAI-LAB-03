package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.objects.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.StreamingVideo;
import ch.heigvd.dai.utils.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UploadCommand extends Command {


    public UploadCommand() {
        super("UPLOAD", "Upload video");
    }

    @Override
    public void validate(String[] args) throws CommandException {
        if (args.length != 2) {
            throw new CommandException("The upload command expects title and description");
        }
    }

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {

        String title = new String(Base64.getDecoder().decode(args[0]), StandardCharsets.UTF_8);
        String description = new String(Base64.getDecoder().decode(args[1]), StandardCharsets.UTF_8);

        if(user == null){
            return new CommandResponse(CommandResponseCode.ERROR, "You must be connected to upload a video");
        }

        try {

            sendResponse(new CommandResponse(CommandResponseCode.OK, "Ready to receive video"));

        }catch (IOException e){

            return new CommandResponse(CommandResponseCode.ERROR, "Error while uploading video: " + e.getMessage());

        }

        System.out.println("Receiving video...");
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);

        String fullFileName = streamingVideo.encodeVideoName(title, description);

        try {

            try (FileOutputStream fos = new FileOutputStream(Utils.SERVER_VIDEO_PATH + "/" + fullFileName)) {
                String line;

                while ((line = in.readLine()) != null) {
                    if (line.equals(Utils.UPLOAD_DELIMITER)) {
                        break;
                    }

                    byte[] chunk = Base64.getDecoder().decode(line);
                    fos.write(chunk);

                }
                System.out.println("Upload complete !");
            }

            streamingVideo.addVideo(new Video(title, description, fullFileName));
            return new CommandResponse(CommandResponseCode.OK, "Video uploaded successfully");

        } catch (Exception e) {
            System.err.println("Error during upload: " + e.getMessage());
            return new CommandResponse(CommandResponseCode.ERROR, "Error uploading video: " + e.getMessage());
        }
    }

    @Override
    public void receive() {
        try {

            CommandResponse response = readResponse();
            System.out.println(response.getMessage());

        } catch (IOException e) {
            System.err.println("Error while uploading video: " + e.getMessage());
        }
    }
}