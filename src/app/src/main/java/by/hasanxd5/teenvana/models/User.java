package by.hasanxd5.teenvana.models;

import java.io.Serializable;

public class User implements Serializable {
    private final String id;
    private final String name;
    private final String avatarUrl;
    private final String bio;
    private final String phoneNumber;
    private final String status;

    public User(String id, String name, String avatarUrl) {
        this(id, name, avatarUrl, "No bio available", "Not provided", "online");
    }

    public User(String id, String name, String avatarUrl, String bio, String phoneNumber, String status) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }
}