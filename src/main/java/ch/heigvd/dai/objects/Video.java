package ch.heigvd.dai.objects;

import ch.heigvd.dai.server.StreamingVideo;

public class Video {

    private final String title;
    private final String description;
    private final String URL;

    public Video(String title, String description, String URL) {
        this.title = title;
        this.description = description;
        this.URL = StreamingVideo.videoPath + "/" + URL;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getURL(){
        return URL;
    }

    public String toString(){
        return title + ": " + description;
    }

}
