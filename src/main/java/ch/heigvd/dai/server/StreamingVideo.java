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
    public static final String videoPath = System.getProperty("user.dir") + "/videos";

    public StreamingVideo(){
        this.resourceManager = new ResourceManager();
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


    // Méthodes déléguées au ResourceManager
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

    // Méthodes pour la gestion de la concurrence
    public boolean canWatchVideo(String videoTitle) {
        return resourceManager.startWatchingVideo(videoTitle);
    }

    public void finishWatchingVideo(String videoTitle) {
        resourceManager.stopWatchingVideo(videoTitle);
    }

    public boolean canDeleteVideo(String videoTitle) {
        return resourceManager.canDeleteVideo(videoTitle);
    }

    // Nouvelle méthode de suppression qui gère aussi le fichier physique
    public void deleteVideo(Video video) {
        // On vérifie d'abord si on peut supprimer la vidéo
        if (canDeleteVideo(video.getTitle())) {
            // On supprime d'abord de la gestion des ressources
            resourceManager.deleteVideo(video.getTitle());

            // Puis on supprime le fichier physique
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

    public boolean userExists(String pseudo, String email) {
        for (User user : getUsers()) {
            if (user.getUsername().equalsIgnoreCase(pseudo) ||
                    user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidChoice(String videoChoice) {
        try {
            int index = Integer.parseInt(videoChoice);
            return index > 0 && index <= getVideos().size();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Video getVideo(String videoChoice) {
        int index = Integer.parseInt(videoChoice);
        return getVideos().get(index - 1);
    }
}
