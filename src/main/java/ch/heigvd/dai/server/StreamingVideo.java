package ch.heigvd.dai.server;

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;

import java.util.ArrayList;
import java.util.List;

public class StreamingVideo {
    private List<User> users;
    private List<Video> videos;

    public StreamingVideo(){
        users = new ArrayList<>();
        videos = new ArrayList<>();
    }

    public void load(){

        String videoPath = System.getProperty("user.dir") + "/videos";

        addVideo(new Video("3 Minute Timer", "Displays a timer from 3 minutes to 0", videoPath + "video1.mp4"));
        addVideo(new Video("Google Office tour", "Visit of Google's building", videoPath + "video2.mp4"));
        addVideo(new Video("L'entretien - Choss", "Vidéo de Choss sur un entretien", videoPath + "video3.mp4"));
        addVideo(new Video("Le Clown - Choss", "Vidéo de Choss sur un clown", videoPath + "video4.mp4"));
        addVideo(new Video("Why is Switzerland home to so many billionaires", "Documentary on Switzerland's billionaires", videoPath + "video5.mp4"));

    }

    public void addUser(User user){
        users.add(user);
    }

    public void addVideo(Video video){
        videos.add(video);
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Video> getVideos() {
        return videos;
    }

    /**
     * Check if the video choice is valid
     * @param videoChoice: index of the chosen video
     * @return true if the index is valid and false otherwise
     */
    public boolean checkValidity(String videoChoice){
        int index = 0;
        try{
            index = Integer.parseInt(videoChoice);
        }
        catch(NumberFormatException e){
            return false;
        }

        return (index > 0 && index <= videos.size());
    }

    public Video getVideo(String videoChoice) {
        int index = Integer.parseInt(videoChoice);
        return videos.get(index - 1);
    }
}
