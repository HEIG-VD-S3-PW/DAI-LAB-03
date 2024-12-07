package ch.heigvd.dai.server;

import ch.heigvd.dai.objects.User;
import ch.heigvd.dai.objects.Video;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.io.File;

import ch.heigvd.dai.utils.Utils;

public class StreamingVideo {

    private final ResourceManager resourceManager;

    public StreamingVideo(){
        this.resourceManager = new ResourceManager();
        this.load();
    }


    public void load() {
        File directory = new File(Utils.SERVER_VIDEO_PATH);
        File[] videoFiles = directory.listFiles();


        if (videoFiles == null) {
            System.err.println("Error : the video directory is unreachable : " + Utils.SERVER_VIDEO_PATH);
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


    // Methods concurrency safe

    public void addUser(User user) {
        resourceManager.addUser(user);
    }

    public void removeUser(User user) {
        resourceManager.removeUser(user);
    }

    public void addVideo(Video video) {
        resourceManager.addVideo(video);
    }

    public List<User> getUsers() {
        return resourceManager.getUsers();
    }

    public List<Video> getVideos() {
        return resourceManager.getVideos();
    }

    public boolean canDownloadVideo(String videoTitle) {
        return resourceManager.startDownloadingVideo(videoTitle);
    }

    public void finishDownloadingVideo(String videoTitle) {
        resourceManager.stopDownloadingVideo(videoTitle);
    }

    public boolean canDeleteVideo(String videoTitle) {
        return resourceManager.canDeleteVideo(videoTitle);
    }


    /**
     * Delete a video from the server
     *
     * @param video the video to delete
     */
    public void deleteVideo(Video video) {
        // Check if the video can be deleted (no active viewers)
        if (canDeleteVideo(video.getTitle())) {
            resourceManager.deleteVideo(video.getTitle());

            try {

                File videoFile = new File(video.getURL());
                if (!videoFile.delete()) {
                    System.err.println("Warning: Could not delete physical file: " + video.getURL());
                }

            } catch (SecurityException e) {

                System.err.println("Security error while deleting file: " + e.getMessage());

            } catch (Exception e) {

                System.err.println("Unexpected error while deleting file: " + e.getMessage());

            }
        }
    }

    /**
     * Check if a user already exists
     *
     * @param pseudo the pseudo of the user
     * @param email the email of the user
     * @return true if the user already exists, false otherwise
     */
    public boolean userExists(String pseudo, String email) {
        for (User user : getUsers()) {
            if (user.getUsername().equalsIgnoreCase(pseudo) ||
                    user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the video choice is valid
     *
     * @param videoChoice the video choice
     * @return true if the choice is valid, false otherwise
     */
    public boolean isValidChoice(String videoChoice) {
        try {
            int index = Integer.parseInt(videoChoice);
            return index > 0 && index <= getVideos().size();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Get the video corresponding to the choice (String)
     *
     * @param videoChoice the video choice
     * @return the video corresponding to the choice
     */
    public Video getVideo(String videoChoice) {
        int index = Integer.parseInt(videoChoice);
        return getVideos().get(index - 1);
    }
}
