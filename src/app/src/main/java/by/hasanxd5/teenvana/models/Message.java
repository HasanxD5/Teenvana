package by.hasanxd5.teenvana.models;

import java.io.Serializable;

public class Message implements Serializable {
    private String id;
    private String senderId;
    private String text;
    private long timestamp;

    public Message(String id, String senderId, String text, long timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
