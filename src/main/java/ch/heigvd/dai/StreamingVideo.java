package ch.heigvd.dai;

import java.util.ArrayList;
import java.util.List;

public class StreamingVideo {
    private List<User> users;
    private List<Video> videos;

    public StreamingVideo(){
        users = new ArrayList<>();
        videos = new ArrayList<>();
    }

    public StreamingVideo(List<User> users, List<Video> videos) {
        this.users = users;
        this.videos = videos;
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
}
