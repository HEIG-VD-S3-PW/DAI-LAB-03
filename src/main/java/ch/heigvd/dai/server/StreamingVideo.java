package ch.heigvd.dai.server;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.objects.Video;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.io.File;

import ch.heigvd.dai.utils.Utils;

public class StreamingVideo {
    private List<User> users;
    private List<Video> videos;

    public static final String videoPath = System.getProperty("user.dir") + "/videos";

    public StreamingVideo(){
        users = new ArrayList<>();
        videos = new ArrayList<>();
        this.load();
    }


    public void load() {
        File directory = new File(videoPath);
        File[] videoFiles = directory.listFiles();

        if (videoFiles == null) {
            System.err.println("Error : the video directory is unreachable : " + videoPath);
            return;
        }

        for (File videoFile : videoFiles) {
            try {
                if (!isValidVideoFile(videoFile)) {
                    System.err.println("Invalid video file (ignored) : " + videoFile.getName());
                    continue;
                }

                Video video = decodeVideoFile(videoFile.getName());
                addVideo(video);

            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public String encodeVideoName(String title, String description) {
        String videoData = title + "|" + description;
        byte[] encodedBytes = Base64.getEncoder().encode(videoData.getBytes());
        return new String(encodedBytes) + ".mp4";
    }

    private Video decodeVideoFile(String fileName) {
        try {
            String encodedName = fileName.substring(0, fileName.lastIndexOf("."));
            byte[] decodedBytes = Base64.getDecoder().decode(encodedName);
            String decodedString = new String(decodedBytes);

            String[] videoData = decodedString.split("\\|");
            if (videoData.length != 2) {
                throw new IllegalArgumentException("Invalid format for " + fileName);
            }

            return new Video(videoData[0], videoData[1], fileName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid format for " + fileName);
        }
    }

    public static boolean isValidVideoFile(File file) {
        return file.isFile() && Utils.getFileExtension(file.getName()).equals("mp4");
    }


    public void addUser(User user){
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
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

    public boolean checkValidity(String videoChoice){

        int index = 0;
        try{
            index = Integer.parseInt(videoChoice);
        } catch(NumberFormatException e){
            return false;
        }

        return (index > 0 && index <= videos.size());
    }

    public Video getVideo(String videoChoice) {
        int index = Integer.parseInt(videoChoice);
        return videos.get(index - 1);
    }
}
