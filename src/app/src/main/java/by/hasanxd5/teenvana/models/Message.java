package by.hasanxd5.teenvana.models;

import java.io.Serializable;

public class Message implements Serializable {
    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_IMAGE = "IMAGE";
    public static final String TYPE_VIDEO = "VIDEO";
    public static final String TYPE_GIF = "GIF";

    private final String id;
    private final String senderId;
    private final String text;
    private final long timestamp;
    private final String type;
    private final String url;

    public Message(String id, String senderId, String text, long timestamp, String type, String url) {
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.type = type != null ? type : TYPE_TEXT;
        this.url = url;
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

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}