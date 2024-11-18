package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;

import ch.heigvd.dai.StreamingVideo;
import ch.heigvd.dai.Video;
import ch.heigvd.dai.User;
import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start the server part.")
public class Server implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "6433")
    protected int port;

    @Override
    public Integer call() {
        StreamingVideo server = initServer();
        throw new UnsupportedOperationException(
                "Please remove this exception and implement this method.");
    }

    public StreamingVideo initServer(){
        String videoPath = System.getProperty("user.dir") + "/videos";
        final int qtVideo = 5;
        StreamingVideo plateforme = new StreamingVideo();

        plateforme.addVideo(new Video("3 Minute Timer", "Displays a timer from 3 minutes to 0", videoPath + "video1.mp4"));
        plateforme.addVideo(new Video("Google Office tour", "Visit of Google's building", videoPath + "video2.mp4"));
        plateforme.addVideo(new Video("L'entretien - Choss", "Vidéo de Choss sur un entretien", videoPath + "video3.mp4"));
        plateforme.addVideo(new Video("Le Clown - Choss", "Vidéo de Choss sur un clown", videoPath + "video4.mp4"));
        plateforme.addVideo(new Video("Why is Switzerland home to so many billionaires", "Documentary on Switzerland's billionaires", videoPath + "video5.mp4"));

        return plateforme;
    }
}