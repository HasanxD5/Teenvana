package by.hasanxd5.teenvana.models;

import java.io.Serializable;

public class User implements Serializable {
    private final String id;
    private final String name;
    private final String avatarUrl;

    public User(String id, String name, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
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
}
