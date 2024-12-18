package ch.heigvd.dai.server;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.objects.Video;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible for managing the resources of the server (thread-safe).
 */
public class ResourceManager {

    private final CopyOnWriteArrayList<User> users;
    private final CopyOnWriteArrayList<Video> videos;

    private final ConcurrentHashMap<String, Integer> activeDownloaders;

    public ResourceManager() {
        this.users = new CopyOnWriteArrayList<>();
        this.videos = new CopyOnWriteArrayList<>();
        this.activeDownloaders = new ConcurrentHashMap<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public CopyOnWriteArrayList<User> getUsers() {
        return users;
    }

    public void addVideo(Video video) {
        videos.add(video);
        activeDownloaders.put(video.getTitle(), 0);
    }

    public CopyOnWriteArrayList<Video> getVideos() {
        return videos;
    }

    public boolean startDownloadingVideo(String videoTitle) {
        return activeDownloaders.computeIfPresent(videoTitle, (key, count) -> count + 1) != null;
    }

    public void stopDownloadingVideo(String videoTitle) {
        activeDownloaders.computeIfPresent(videoTitle, (key, count) -> Math.max(0, count - 1));
    }

    public boolean canDeleteVideo(String videoTitle) {
        Integer viewers = activeDownloaders.get(videoTitle);
        return viewers != null && viewers == 0;
    }

    public void deleteVideo(String videoTitle) {
        videos.removeIf(v -> v.getTitle().equals(videoTitle));
        activeDownloaders.remove(videoTitle);
    }
}