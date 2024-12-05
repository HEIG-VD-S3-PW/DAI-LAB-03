package ch.heigvd.dai.server;

import ch.heigvd.dai.User;
import ch.heigvd.dai.Video;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.io.File;

public class StreamingVideo {
    private List<User> users;
    private List<Video> videos;

    public static final String videoPath = System.getProperty("user.dir") + "/videos";

    public StreamingVideo(){
        users = new ArrayList<>();
        videos = new ArrayList<>();
        this.load();
    }

    String getFileExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex >= 0) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }

    public void load(){

        File directory = new File(videoPath);

        File[] videos = directory.listFiles();

        assert videos != null;
        for(File video : videos){
            if(!getFileExtension(video.getName()).equals("mp4")){
                System.err.println("Invalid format: " + video.getName());
                return;
            }
            String encodedString = video.getName().substring(0, video.getName().lastIndexOf("."));
            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
            String decodedString = new String(decodedBytes);

            String[] videoData = decodedString.split("\\|");

            if(videoData.length != 2){
                System.err.println("Invalid format: " + video.getName());
                return;
            }
            addVideo(new Video (videoData[0], videoData[1], video.getName()));
        }

    }

    public void addUser(User user){
        users.add(user);
    }

    public boolean suppressUser(User user) {
        return users.remove(user);
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

    public boolean userExists(String pseudo, String email){
        for(User user : users){
            if(user.getUsername().equalsIgnoreCase(pseudo) || user.getEmail().equalsIgnoreCase(email)){
                return true;
            }
        }
        return false;
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
