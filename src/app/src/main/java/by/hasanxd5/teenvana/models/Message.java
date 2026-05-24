package by.hasanxd5.teenvana.models;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_IMAGE = "IMAGE";
    public static final String TYPE_VIDEO = "VIDEO";
    public static final String TYPE_GIF = "GIF";
    public static final String TYPE_COLLAGE = "COLLAGE";

    private final String id;
    private final String senderId;
    private String text;
    private final long timestamp;
    private final String type;
    private final String url;
    private final List<String> mediaUrls;

    public Message(String id, String senderId, String text, long timestamp, String type, String url) {
        this(id, senderId, text, timestamp, type, url, null);
    }

    public Message(String id, String senderId, String text, long timestamp, String type, String url, List<String> mediaUrls) {
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.type = type != null ? type : TYPE_TEXT;
        this.url = url;
        this.mediaUrls = mediaUrls;
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

    public void setText(String text) {
        this.text = text;
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

    public List<String> getMediaUrls() {
        return mediaUrls;
    }
}