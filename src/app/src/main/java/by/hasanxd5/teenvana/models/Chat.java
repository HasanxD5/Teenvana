package by.hasanxd5.teenvana.models;

import java.io.Serializable;

public class Chat implements Serializable {
    private String id;
    private User otherUser;
    private Message lastMessage;
    private int unreadCount;

    public Chat(String id, User otherUser, Message lastMessage, int unreadCount) {
        this.id = id;
        this.otherUser = otherUser;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    public String getId() {
        return id;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
}
