

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
import java.nio.charset.StandardCharsets;
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
        String title = new String(Base64.getDecoder().decode(args[0]), StandardCharsets.UTF_8);
        String description = new String(Base64.getDecoder().decode(args[1]), StandardCharsets.UTF_8);

        System.out.println("Uploading video: " + title + " with description: " + description);

        // Création du nom de fichier encodé
        String fileData = title + "|" + description;
        String encodedFileName = Base64.getEncoder().encodeToString(fileData.getBytes(StandardCharsets.UTF_8));
        String fullFileName = encodedFileName + ".mp4";

        try (FileOutputStream fos = new FileOutputStream("videos/" + fullFileName)) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("END_OF_UPLOAD")) {
                    break;
                }
                byte[] chunk = Base64.getDecoder().decode(line);
                fos.write(chunk);
            }
            fos.flush();
        } catch (Exception e) {
            System.err.println("Error while uploading video: " + e.getMessage());
            return new CommandResponse(CommandResponseCode.ERROR, "Error uploading video: " + e.getMessage());
        }

        streamingVideo.addVideo(new Video(title, description, fullFileName));

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

