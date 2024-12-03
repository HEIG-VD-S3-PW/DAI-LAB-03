

package ch.heigvd.dai.protocol.commands;

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.protocol.Command;
import ch.heigvd.dai.protocol.CommandException;
import ch.heigvd.dai.protocol.CommandResponse;
import ch.heigvd.dai.protocol.CommandResponseCode;
import ch.heigvd.dai.server.StreamingVideo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class UploadCommand extends Command {

    public UploadCommand(){
        super("UPLOAD", "Upload video");
    }

    @Override
    public void validate(String[] args) throws CommandException {

    }

    @Override
    public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {
        String title = Arrays.toString(Base64.getDecoder().decode(args[0]));
        String description = Arrays.toString(Base64.getDecoder().decode(args[1]));

        System.out.println("Uploading video: " + title + " with description: " + description);

        try (FileOutputStream fos = new FileOutputStream("videos/" + title)) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("END_OF_UPLOAD")) {
                    break;
                }
                // Décoder et écrire uniquement les lignes qui ne sont pas des marqueurs
                byte[] chunk = Base64.getDecoder().decode(line);
                fos.write(chunk);
            }
            fos.flush();
        } catch (Exception e) {
            System.err.println("Error while uploading video: " + e.getMessage());
            return new CommandResponse(CommandResponseCode.ERROR, "Error uploading video: " + e.getMessage());
        }

        streamingVideo.addVideo(new Video(title, description, "videos/" + title));

        return new CommandResponse(CommandResponseCode.OK, "Video uploaded successfully");
    }

    @Override
    public void receive() {

        try {
            CommandResponse response = readResponse();

            if(response.getCode() != 200){
                System.err.println("Error while uploading video: " + response.getMessage());
                return;
            }

            System.out.println(response.getMessage());

        } catch (IOException e) {
            System.err.println("Error while uploading video: " + e.getMessage());
        }

    }
}

