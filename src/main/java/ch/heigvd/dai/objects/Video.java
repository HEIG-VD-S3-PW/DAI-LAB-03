package ch.heigvd.dai.objects;

import ch.heigvd.dai.server.StreamingVideo;

public class Video {
    private String title;
    private String description;
    private String URL;

    public Video(String title, String description, String URL) {
        this.title = title;
        this.description = description;
        this.URL = StreamingVideo.videoPath + "/" + URL;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getURL(){
        return URL;
    }

    public void setURL(String URL){
        this.URL = URL;
    }

    public String toString(){
        return title + ": " + description;
    }
}
